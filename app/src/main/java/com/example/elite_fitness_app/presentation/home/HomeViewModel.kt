package com.example.elite_fitness_app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.TrainingPlan
import com.example.elite_fitness_app.domain.model.User
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.domain.repository.TrainingPlanRepository
import com.example.elite_fitness_app.domain.repository.WorkoutRepository
import com.example.elite_fitness_app.domain.repository.UserRepository
import com.example.elite_fitness_app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val workouts: List<Workout> = emptyList(),
    val trainingPlans: List<TrainingPlan> = emptyList(),
    val stats: Map<String, Any> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val trainingPlanRepository: TrainingPlanRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Parallel fetches or sequential handling
            val profileRes = userRepository.getProfile()
            val workoutsRes = workoutRepository.getWorkouts()
            val plansRes = trainingPlanRepository.getPlans()
            val statsRes = sessionRepository.getStats()

            var errorMsg: String? = null
            var user: User? = _uiState.value.user
            var workouts = _uiState.value.workouts
            var plans = _uiState.value.trainingPlans
            var stats = _uiState.value.stats

            if (profileRes is Resource.Success) {
                user = profileRes.data
            } else if (profileRes is Resource.Error) {
                errorMsg = profileRes.message
            }

            if (workoutsRes is Resource.Success) {
                workouts = workoutsRes.data
            }

            if (plansRes is Resource.Success) {
                plans = plansRes.data
            }

            if (statsRes is Resource.Success) {
                stats = statsRes.data
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    user = user,
                    workouts = workouts,
                    trainingPlans = plans,
                    stats = stats,
                    error = errorMsg
                )
            }
        }
    }
}
