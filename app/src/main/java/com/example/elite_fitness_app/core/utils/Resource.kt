package com.example.elite_fitness_app.core.utils

/**
 * Generic wrapper for API/data responses.
 * Allows ViewModels to handle Loading, Success, and Error states uniformly.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val code: Int? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
