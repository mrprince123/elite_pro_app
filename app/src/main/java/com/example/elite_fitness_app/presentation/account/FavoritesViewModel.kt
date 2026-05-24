package com.example.elite_fitness_app.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.Exercise
import com.example.elite_fitness_app.domain.model.Favorite
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.domain.repository.ExerciseRepository
import com.example.elite_fitness_app.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favorites: List<Favorite> = emptyList(),
    val likedWorkouts: List<Workout> = emptyList(),
    val likedExercises: List<Exercise> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val favResult = exerciseRepository.getFavorites()) {
                is Resource.Success -> {
                    val favoritesList = favResult.data

                    // Load workouts
                    val workoutsResult = workoutRepository.getWorkouts()
                    val workouts = if (workoutsResult is Resource.Success) {
                        val favWorkoutIds = favoritesList.filter { it.type == "workout" }.map { it.itemId }
                        workoutsResult.data.filter { it.id in favWorkoutIds }
                    } else {
                        emptyList()
                    }

                    // Load exercises in parallel
                    val favExerciseIds = favoritesList.filter { it.type == "exercise" }.map { it.itemId }
                    val exercises = favExerciseIds.map { itemId ->
                        async { exerciseRepository.getExerciseById(itemId) }
                    }.awaitAll().mapNotNull { result ->
                        if (result is Resource.Success) result.data else null
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            favorites = favoritesList,
                            likedWorkouts = workouts,
                            likedExercises = exercises
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = favResult.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun toggleFavoriteWorkout(workoutId: String) {
        val favorite = _uiState.value.favorites.find { it.itemId == workoutId && it.type == "workout" } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = exerciseRepository.removeFavorite(favorite.id)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            favorites = state.favorites.filterNot { it.id == favorite.id },
                            likedWorkouts = state.likedWorkouts.filterNot { it.id == workoutId }
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

    fun toggleFavoriteExercise(exerciseId: String) {
        val favorite = _uiState.value.favorites.find { it.itemId == exerciseId && it.type == "exercise" } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = exerciseRepository.removeFavorite(favorite.id)) {
                is Resource.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            favorites = state.favorites.filterNot { it.id == favorite.id },
                            likedExercises = state.likedExercises.filterNot { it.id == exerciseId }
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
}
