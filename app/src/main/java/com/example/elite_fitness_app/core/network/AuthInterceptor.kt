package com.example.elite_fitness_app.core.network

import com.example.elite_fitness_app.core.datastore.AppDataStore
import com.example.elite_fitness_app.core.utils.Constants
import com.example.elite_fitness_app.data.dto.ApiResponse
import com.example.elite_fitness_app.data.dto.AuthResponseDto
import com.example.elite_fitness_app.data.dto.RefreshTokenRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that attaches the JWT Bearer token to every outgoing request.
 * Skips auth endpoints (login, register) that don't require authentication.
 * Automatically refreshes the access token when encountering a 401 response.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val dataStore: AppDataStore
) : Interceptor {

    companion object {
        private val SKIP_AUTH_PATHS = listOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh-token"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Skip auth for public/auth endpoints
        if (SKIP_AUTH_PATHS.any { path.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { dataStore.jwtToken.firstOrNull() }

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(newRequest)

        if (response.code == 401) {
            // Synchronously handle token refresh to avoid concurrent attempts
            synchronized(this) {
                // Check if another thread already refreshed the token
                val currentToken = runBlocking { dataStore.jwtToken.firstOrNull() }

                if (currentToken != token && !currentToken.isNullOrBlank()) {
                    response.close()
                    val retryRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                    return chain.proceed(retryRequest)
                }

                val refresh = runBlocking { dataStore.refreshToken.firstOrNull() }
                if (!refresh.isNullOrBlank()) {
                    response.close() // Close the original 401 response

                    val okHttpClient = OkHttpClient()
                    val jsonMediaType = "application/json; charset=utf-8".toMediaType()
                    val requestBody = Gson().toJson(RefreshTokenRequest(refresh)).toRequestBody(jsonMediaType)

                    val refreshRequest = Request.Builder()
                        .url("${Constants.BASE_URL}auth/refresh-token")
                        .post(requestBody)
                        .build()

                    try {
                        val refreshResponse = okHttpClient.newCall(refreshRequest).execute()
                        if (refreshResponse.isSuccessful) {
                            val bodyString = refreshResponse.body?.string()
                            val type = object : com.google.gson.reflect.TypeToken<ApiResponse<AuthResponseDto>>() {}.type
                            val apiResponse = Gson().fromJson<ApiResponse<AuthResponseDto>>(bodyString, type)
                            val newData = apiResponse.data

                            if (newData != null) {
                                runBlocking {
                                    dataStore.saveTokens(newData.accessToken, newData.refreshToken)
                                }
                                val retryRequest = originalRequest.newBuilder()
                                    .header("Authorization", "Bearer ${newData.accessToken}")
                                    .build()
                                return chain.proceed(retryRequest)
                            }
                        } else {
                            // Refresh token is also expired or invalid, log out the user
                            runBlocking {
                                dataStore.clearAll()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        return response
    }
}
