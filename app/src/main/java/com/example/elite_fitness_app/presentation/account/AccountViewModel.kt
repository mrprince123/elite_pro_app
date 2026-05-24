package com.example.elite_fitness_app.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.User
import com.example.elite_fitness_app.domain.repository.AuthRepository
import com.example.elite_fitness_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val success: Boolean = false,
    val editName: String = "",
    val editPhone: String = "",
    val editHeight: String = "",
    val editWeight: String = "",
    val editGoal: String = "",
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = userRepository.getProfile()) {
                is Resource.Success -> {
                    val u = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = u,
                            editName = u.name,
                            editPhone = u.phone,
                            editHeight = u.height?.toString() ?: "",
                            editWeight = u.weight?.toString() ?: "",
                            editGoal = u.fitnessGoal ?: ""
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(editName = name) }
    fun updatePhone(phone: String) = _uiState.update { it.copy(editPhone = phone) }
    fun updateHeight(h: String) = _uiState.update { it.copy(editHeight = h) }
    fun updateWeight(w: String) = _uiState.update { it.copy(editWeight = w) }
    fun updateGoal(g: String) = _uiState.update { it.copy(editGoal = g) }

    fun saveProfile() {
        val state = _uiState.value
        val currentUser = state.user ?: return
        val updatedUser = currentUser.copy(
            name = state.editName,
            phone = state.editPhone,
            height = state.editHeight.toDoubleOrNull(),
            weight = state.editWeight.toDoubleOrNull(),
            fitnessGoal = state.editGoal
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }
            when (val result = userRepository.updateProfile(updatedUser)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = result.data,
                            success = true
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.logout()
            _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
            onSuccess()
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = userRepository.deleteAccount()
            if (result is Resource.Success) {
                authRepository.logout()
                _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
                onSuccess()
            } else if (result is Resource.Error) {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
