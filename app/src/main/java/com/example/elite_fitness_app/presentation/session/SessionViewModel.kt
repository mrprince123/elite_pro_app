package com.example.elite_fitness_app.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.domain.model.WorkoutSession
import com.example.elite_fitness_app.domain.repository.SessionRepository
import com.example.elite_fitness_app.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionUiState(
    val isLoading: Boolean = false,
    val workout: Workout? = null,
    val currentSession: WorkoutSession? = null,
    val activeExerciseIndex: Int = 0,
    val completedSetsCount: Int = 0,
    val elapsedSeconds: Int = 0,
    val error: String? = null,
    val sessionFinished: Boolean = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startSession(workoutId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // 1. Fetch workout details
            val workoutRes = workoutRepository.getWorkoutById(workoutId)
            if (workoutRes is Resource.Success) {
                _uiState.update { it.copy(workout = workoutRes.data) }
                
                // 2. Call API to start session
                when (val sessionRes = sessionRepository.startSession(workoutId)) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                currentSession = sessionRes.data,
                                elapsedSeconds = 0,
                                activeExerciseIndex = 0
                            )
                        }
                        startTimer()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = sessionRes.message) }
                    }
                    is Resource.Loading -> {}
                }
            } else if (workoutRes is Resource.Error) {
                _uiState.update { it.copy(isLoading = false, error = workoutRes.message) }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun nextExercise() {
        val workout = _uiState.value.workout ?: return
        val currentIndex = _uiState.value.activeExerciseIndex
        if (currentIndex < workout.exercises.lastIndex) {
            _uiState.update { it.copy(activeExerciseIndex = currentIndex + 1) }
        }
    }

    fun prevExercise() {
        val currentIndex = _uiState.value.activeExerciseIndex
        if (currentIndex > 0) {
            _uiState.update { it.copy(activeExerciseIndex = currentIndex - 1) }
        }
    }

    fun completeSet() {
        _uiState.update { it.copy(completedSetsCount = it.completedSetsCount + 1) }
    }

    fun endSession(notes: String, onSuccess: () -> Unit) {
        timerJob?.cancel()
        val session = _uiState.value.currentSession ?: return
        _uiState.value.workout ?: return
        val elapsed = _uiState.value.elapsedSeconds
        
        // Approximate calories: 6 kcal per minute as standard
        val minutes = elapsed / 60
        val calories = minutes * 8

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val completedEx = _uiState.value.activeExerciseIndex + 1
            when (val result = sessionRepository.endSession(
                sessionId = session.id,
                caloriesBurned = calories,
                completedExercises = completedEx,
                notes = notes
            )) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, sessionFinished = true) }
                    onSuccess()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
