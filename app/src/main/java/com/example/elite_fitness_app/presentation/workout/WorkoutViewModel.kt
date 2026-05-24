package com.example.elite_fitness_app.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.domain.model.WorkoutExercise
import com.example.elite_fitness_app.domain.model.Favorite
import com.example.elite_fitness_app.domain.repository.WorkoutRepository
import com.example.elite_fitness_app.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutUiState(
    val isLoading: Boolean = false,
    val workouts: List<Workout> = emptyList(),
    val currentWorkout: Workout? = null,
    val error: String? = null,
    
    // Creating/Editing state variables
    val newWorkoutName: String = "",
    val newWorkoutDesc: String = "",
    val newWorkoutDifficulty: String = "Intermediate",
    val newWorkoutDuration: Int = 45,
    val addedExercises: List<WorkoutExercise> = emptyList(),
    val shouldNavigateToLibrary: Boolean = false,
    val favorites: List<Favorite> = emptyList()
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadWorkouts()
        loadFavorites()
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = workoutRepository.getWorkouts()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, workouts = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadWorkoutById(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = workoutRepository.getWorkoutById(id)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, currentWorkout = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    // ─── Workout Creation Helpers ──────────────────────────────────────────
    fun updateName(name: String) = _uiState.update { it.copy(newWorkoutName = name) }
    fun updateDesc(desc: String) = _uiState.update { it.copy(newWorkoutDesc = desc) }
    fun updateDifficulty(diff: String) = _uiState.update { it.copy(newWorkoutDifficulty = diff) }
    fun updateDuration(duration: Int) = _uiState.update { it.copy(newWorkoutDuration = duration) }
    fun setNavigateToLibrary(shouldNavigate: Boolean) = _uiState.update { it.copy(shouldNavigateToLibrary = shouldNavigate) }

    fun addExerciseToNewWorkout(exerciseId: String, name: String) {
        val exercise = WorkoutExercise(
            exerciseId = exerciseId,
            exerciseName = name,
            sets = 3,
            reps = 12,
            restTime = 60,
            order = _uiState.value.addedExercises.size
        )
        _uiState.update { it.copy(addedExercises = it.addedExercises + exercise) }
    }

    fun removeExerciseFromNewWorkout(index: Int) {
        val list = _uiState.value.addedExercises.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            // Re-order
            val updated = list.mapIndexed { idx, ex -> ex.copy(order = idx) }
            _uiState.update { it.copy(addedExercises = updated) }
        }
    }

    fun updateExerciseSets(index: Int, sets: Int) {
        val list = _uiState.value.addedExercises.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(sets = sets)
            _uiState.update { it.copy(addedExercises = list) }
        }
    }

    fun updateExerciseReps(index: Int, reps: Int) {
        val list = _uiState.value.addedExercises.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(reps = reps)
            _uiState.update { it.copy(addedExercises = list) }
        }
    }

    fun createWorkout(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.newWorkoutName.isBlank()) {
            _uiState.update { it.copy(error = "Workout name cannot be empty") }
            return
        }

        val backendDifficulty = when (state.newWorkoutDifficulty.trim().lowercase()) {
            "beginner" -> "beginner"
            "advanced" -> "advanced"
            else -> "intermediate"
        }

        val newWorkout = Workout(
            name = state.newWorkoutName,
            description = state.newWorkoutDesc,
            difficulty = backendDifficulty,
            estimatedDuration = state.newWorkoutDuration,
            exercises = state.addedExercises
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = workoutRepository.createWorkout(newWorkout)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            newWorkoutName = "",
                            newWorkoutDesc = "",
                            newWorkoutDifficulty = "Intermediate",
                            addedExercises = emptyList()
                        )
                    }
                    loadWorkouts()
                    onSuccess()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteWorkout(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = workoutRepository.deleteWorkout(id)) {
                is Resource.Success -> {
                    loadWorkouts()
                    onSuccess()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun addExerciseToCurrentWorkout(exerciseId: String, name: String, onSuccess: () -> Unit) {
        val current = _uiState.value.currentWorkout ?: return
        val newExercise = WorkoutExercise(
            exerciseId = exerciseId,
            exerciseName = name,
            sets = 3,
            reps = 12,
            restTime = 60,
            order = current.exercises.size
        )
        val updatedWorkout = current.copy(exercises = current.exercises + newExercise)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = workoutRepository.updateWorkout(current.id, updatedWorkout)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, currentWorkout = result.data) }
                    loadWorkouts()
                    onSuccess()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteExerciseFromCurrentWorkout(exerciseId: String) {
        val current = _uiState.value.currentWorkout ?: return
        val updatedExercises = current.exercises.filterNot { it.exerciseId == exerciseId }
            .mapIndexed { idx, ex -> ex.copy(order = idx) }
        val updatedWorkout = current.copy(exercises = updatedExercises)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = workoutRepository.updateWorkout(current.id, updatedWorkout)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, currentWorkout = result.data) }
                    loadWorkouts()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            when (val result = exerciseRepository.getFavorites()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(favorites = result.data) }
                }
                else -> {}
            }
        }
    }

    fun toggleFavorite(workoutId: String) {
        val currentFavorites = _uiState.value.favorites
        val existingFavorite = currentFavorites.find { it.itemId == workoutId && it.type == "workout" }
        
        viewModelScope.launch {
            if (existingFavorite != null) {
                // Already favorited, remove it
                when (val result = exerciseRepository.removeFavorite(existingFavorite.id)) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                favorites = state.favorites.filterNot { it.id == existingFavorite.id }
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            } else {
                // Not favorited yet, add it
                when (val result = exerciseRepository.addFavorite(workoutId, "workout")) {
                    is Resource.Success -> {
                        _uiState.update { state ->
                            state.copy(favorites = state.favorites + result.data)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
}
