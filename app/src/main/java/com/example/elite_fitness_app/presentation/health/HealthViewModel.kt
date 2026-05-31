package com.example.elite_fitness_app.presentation.health

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.HealthConnectManager
import com.example.elite_fitness_app.core.utils.HealthData
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.User
import com.example.elite_fitness_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HealthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val todayData: HealthData = HealthData(),
    val weeklyData: List<HealthData> = emptyList(),
    val bmi: Double = 0.0,
    val bmiCategory: String = "",
    val error: String? = null,
    val isSyncing: Boolean = false,
    val healthSynced: Boolean = false
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = userRepository.getProfile()) {
                is Resource.Success -> {
                    val user = result.data
                    val bmiVal = calculateBmi(user?.weight, user?.height)
                    val bmiCat = getBmiCategory(bmiVal)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            bmi = bmiVal,
                            bmiCategory = bmiCat
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

    fun syncHealthData(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val today = healthConnectManager.readTodayData(context)
            val weekly = healthConnectManager.readWeeklyData(context)
            _uiState.update {
                it.copy(
                    todayData = today,
                    weeklyData = weekly,
                    isSyncing = false,
                    healthSynced = true
                )
            }
        }
    }

    fun isHealthConnectAvailable(context: Context): Boolean {
        return healthConnectManager.isAvailable(context)
    }

    suspend fun hasHealthPermissions(context: Context): Boolean {
        return healthConnectManager.hasPermissions(context)
    }

    private fun calculateBmi(weightKg: Double?, heightCm: Double?): Double {
        if (weightKg == null || heightCm == null || heightCm <= 0) return 0.0
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

    private fun getBmiCategory(bmi: Double): String {
        return when {
            bmi <= 0.0 -> "Unknown"
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal weight"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }
}
