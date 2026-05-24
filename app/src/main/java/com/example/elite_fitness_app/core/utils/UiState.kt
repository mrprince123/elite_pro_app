package com.example.elite_fitness_app.core.utils

/**
 * UI state wrapper for screens.
 * ViewModels expose this to the UI layer to drive rendering.
 */
data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null
)
