package com.example.elite_fitness_app.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.datastore.AppDataStore
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordLoading: Boolean = false,
    val passwordSuccess: String? = null,
    val passwordError: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appDataStore: AppDataStore
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = appDataStore.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isNotificationsEnabled: StateFlow<Boolean> = appDataStore.isNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            appDataStore.setDarkMode(enabled)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            appDataStore.setNotificationsEnabled(enabled)
        }
    }

    fun updateOldPassword(p: String) = _uiState.update { it.copy(oldPassword = p, passwordError = null, passwordSuccess = null) }
    fun updateNewPassword(p: String) = _uiState.update { it.copy(newPassword = p, passwordError = null, passwordSuccess = null) }
    fun updateConfirmPassword(p: String) = _uiState.update { it.copy(confirmPassword = p, passwordError = null, passwordSuccess = null) }

    fun changePassword() {
        val state = _uiState.value
        if (state.oldPassword.isBlank() || state.newPassword.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(passwordError = "All password fields are required") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(passwordError = "Passwords do not match") }
            return
        }
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(passwordError = "New password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPasswordLoading = true, passwordError = null, passwordSuccess = null) }
            when (val result = userRepository.changePassword(state.oldPassword, state.newPassword)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isPasswordLoading = false,
                            oldPassword = "",
                            newPassword = "",
                            confirmPassword = "",
                            passwordSuccess = "Password updated successfully"
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isPasswordLoading = false,
                            passwordError = result.message ?: "Failed to update password"
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
