package com.example.elite_fitness_app.core.utils

/**
 * Constants used across the application.
 */
object Constants {
    // TODO: Replace with your actual backend URL
    const val BASE_URL = "https://elite-pro-back.onrender.com/api/"

    // DataStore
    const val DATASTORE_NAME = "elite_fitness_prefs"
    const val KEY_JWT_TOKEN = "jwt_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_NOTIFICATIONS = "notifications_enabled"
    const val KEY_USER_ID = "user_id"

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20

    // Timeouts (seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
