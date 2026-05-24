package com.example.elite_fitness_app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.elite_fitness_app.core.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DATASTORE_NAME
)

/**
 * Centralized DataStore manager for JWT tokens, user preferences, and settings.
 */
@Singleton
class AppDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val JWT_TOKEN = stringPreferencesKey(Constants.KEY_JWT_TOKEN)
        val REFRESH_TOKEN = stringPreferencesKey(Constants.KEY_REFRESH_TOKEN)
        val DARK_MODE = booleanPreferencesKey(Constants.KEY_DARK_MODE)
        val NOTIFICATIONS = booleanPreferencesKey(Constants.KEY_NOTIFICATIONS)
        val USER_ID = stringPreferencesKey(Constants.KEY_USER_ID)
    }

    // ─── Token Management ─────────────────────────────────────────────────

    val jwtToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.JWT_TOKEN]
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.REFRESH_TOKEN]
    }

    suspend fun saveTokens(jwt: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.JWT_TOKEN] = jwt
            prefs[Keys.REFRESH_TOKEN] = refresh
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.JWT_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
        }
    }

    // ─── User ID ──────────────────────────────────────────────────────────

    val userId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID]
    }

    suspend fun saveUserId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = id
        }
    }

    // ─── Preferences ──────────────────────────────────────────────────────

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: true // default dark
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATIONS] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS] = enabled
        }
    }

    // ─── Clear All (Logout) ──────────────────────────────────────────────

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
