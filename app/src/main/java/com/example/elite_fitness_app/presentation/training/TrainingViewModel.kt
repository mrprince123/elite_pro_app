package com.example.elite_fitness_app.presentation.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.TrainingPlan
import com.example.elite_fitness_app.domain.repository.TrainingPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainingUiState(
    val isLoading: Boolean = false,
    val trainingPlans: List<TrainingPlan> = emptyList(),
    val currentPlan: TrainingPlan? = null,
    val error: String? = null,
    val selectedPart: String = "All",
    val parts: List<String> = listOf("All", "chest", "back", "shoulders", "legs", "cardio")
)

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val trainingPlanRepository: TrainingPlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val selected = _uiState.value.selectedPart
            val result = if (selected == "All") {
                trainingPlanRepository.getPlans()
            } else {
                trainingPlanRepository.getPlansByBodyPart(selected)
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, trainingPlans = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadPlanById(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = trainingPlanRepository.getPlanById(id)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, currentPlan = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun selectPart(part: String) {
        _uiState.update { it.copy(selectedPart = part) }
        loadPlans()
    }
}
