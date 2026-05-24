package com.example.elite_fitness_app.core.network

import com.google.gson.JsonParseException
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Safe API call wrapper that catches all exceptions and returns Resource.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): com.example.elite_fitness_app.core.utils.Resource<T> {
    return try {
        com.example.elite_fitness_app.core.utils.Resource.Success(apiCall())
    } catch (e: HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        val message = parseErrorMessage(errorBody) ?: "Server error: ${e.code()}"
        Timber.e(e, "HTTP Error: ${e.code()} - $message")
        com.example.elite_fitness_app.core.utils.Resource.Error(message, e.code())
    } catch (e: SocketTimeoutException) {
        Timber.e(e, "Timeout error")
        com.example.elite_fitness_app.core.utils.Resource.Error("Connection timed out. Please try again.")
    } catch (e: IOException) {
        Timber.e(e, "Network error")
        com.example.elite_fitness_app.core.utils.Resource.Error("Network error. Please check your connection.")
    } catch (e: JsonParseException) {
        Timber.e(e, "JSON parse error")
        com.example.elite_fitness_app.core.utils.Resource.Error("Failed to parse server response.")
    } catch (e: Exception) {
        Timber.e(e, "Unexpected error")
        com.example.elite_fitness_app.core.utils.Resource.Error(e.message ?: "An unexpected error occurred.")
    }
}

private fun parseErrorMessage(errorBody: String?): String? {
    if (errorBody.isNullOrBlank()) return null
    return try {
        val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
        json.get("message")?.asString
    } catch (e: Exception) {
        null
    }
}
