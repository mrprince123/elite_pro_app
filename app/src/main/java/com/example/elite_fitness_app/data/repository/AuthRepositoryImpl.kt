package com.example.elite_fitness_app.data.repository

import com.example.elite_fitness_app.core.datastore.AppDataStore
import com.example.elite_fitness_app.core.network.safeApiCall
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.data.dto.LoginRequest
import com.example.elite_fitness_app.data.dto.RefreshTokenRequest
import com.example.elite_fitness_app.data.dto.RegisterRequest
import com.example.elite_fitness_app.data.mapper.toDomain
import com.example.elite_fitness_app.data.remote.ApiService
import com.example.elite_fitness_app.domain.model.AuthResponse
import com.example.elite_fitness_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dataStore: AppDataStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthResponse> {
        val result = safeApiCall { api.login(LoginRequest(email, password)) }
        return when (result) {
            is Resource.Success -> {
                val data = result.data.data!!.toDomain()
                dataStore.saveTokens(data.accessToken, data.refreshToken)
                dataStore.saveUserId(data.user.id)
                Resource.Success(data)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun register(
        name: String, email: String, phone: String, password: String,
        height: Double?, weight: Double?, fitnessGoal: String?, age: Int?
    ): Resource<AuthResponse> {
        val result = safeApiCall {
            api.register(RegisterRequest(name, email, phone, password, height, weight, fitnessGoal, age))
        }
        return when (result) {
            is Resource.Success -> {
                val data = result.data.data!!.toDomain()
                dataStore.saveTokens(data.accessToken, data.refreshToken)
                dataStore.saveUserId(data.user.id)
                Resource.Success(data)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun refreshToken(refreshToken: String): Resource<AuthResponse> {
        val result = safeApiCall { api.refreshToken(RefreshTokenRequest(refreshToken)) }
        return when (result) {
            is Resource.Success -> {
                val data = result.data.data!!.toDomain()
                dataStore.saveTokens(data.accessToken, data.refreshToken)
                Resource.Success(data)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun logout(): Resource<Unit> {
        val result = safeApiCall { api.logout() }
        dataStore.clearAll()
        return when (result) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> {
                // Even if API fails, clear local data
                Resource.Success(Unit)
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return !dataStore.jwtToken.firstOrNull().isNullOrBlank()
    }
}
