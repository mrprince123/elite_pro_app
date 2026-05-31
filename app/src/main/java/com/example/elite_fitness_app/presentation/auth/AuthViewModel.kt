package com.example.elite_fitness_app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.AuthResponse
import com.example.elite_fitness_app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val authResponse: AuthResponse? = null,
    val error: String? = null,
    // Login form
    val loginEmail: String = "",
    val loginPassword: String = "",
    // Register form
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPhone: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    val registerHeight: String = "",
    val registerWeight: String = "",
    val registerFitnessGoal: String = "",
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val loggedIn = authRepository.isLoggedIn()
            _uiState.update { it.copy(isLoggedIn = loggedIn) }
        }
    }

    // ─── Login ────────────────────────────────────────────────────────────
    fun updateLoginEmail(email: String) = _uiState.update { it.copy(loginEmail = email) }
    fun updateLoginPassword(password: String) = _uiState.update { it.copy(loginPassword = password) }

    fun login() {
        val state = _uiState.value
        if (state.loginEmail.isBlank() || state.loginPassword.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.login(state.loginEmail.trim(), state.loginPassword)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = true, authResponse = result.data)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    // ─── Register ─────────────────────────────────────────────────────────
    fun updateRegisterName(name: String) = _uiState.update { it.copy(registerName = name) }
    fun updateRegisterEmail(email: String) = _uiState.update { it.copy(registerEmail = email) }
    fun updateRegisterPhone(phone: String) = _uiState.update { it.copy(registerPhone = phone) }
    fun updateRegisterPassword(password: String) = _uiState.update { it.copy(registerPassword = password) }
    fun updateRegisterConfirmPassword(password: String) = _uiState.update { it.copy(registerConfirmPassword = password) }
    fun updateRegisterHeight(height: String) = _uiState.update { it.copy(registerHeight = height) }
    fun updateRegisterWeight(weight: String) = _uiState.update { it.copy(registerWeight = weight) }
    fun updateRegisterFitnessGoal(goal: String) = _uiState.update { it.copy(registerFitnessGoal = goal) }

    fun register() {
        val state = _uiState.value
        if (state.registerName.isBlank() || state.registerEmail.isBlank() || state.registerPassword.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all required fields") }
            return
        }
        if (state.registerPassword != state.registerConfirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }
        val apiGoal = when (state.registerFitnessGoal.trim().lowercase()) {
            "weight loss", "weight_loss" -> "weight_loss"
            "muscle gain", "muscle_gain" -> "muscle_gain"
            "endurance" -> "endurance"
            "flexibility" -> "flexibility"
            "general fitness", "general_fitness" -> "general_fitness"
            else -> "general_fitness"
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = authRepository.register(
                name = state.registerName.trim(),
                email = state.registerEmail.trim(),
                phone = state.registerPhone.trim(),
                password = state.registerPassword,
                height = state.registerHeight.toDoubleOrNull(),
                weight = state.registerWeight.toDoubleOrNull(),
                fitnessGoal = apiGoal,
            )) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = true, authResponse = result.data)
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
