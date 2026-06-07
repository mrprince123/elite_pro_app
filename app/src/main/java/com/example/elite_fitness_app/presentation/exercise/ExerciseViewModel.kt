package com.example.elite_fitness_app.presentation.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.Exercise
import com.example.elite_fitness_app.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseUiState(
    val isLoading: Boolean = false,
    val isPaginatedLoading: Boolean = false,
    val exercises: List<Exercise> = emptyList(),
    val currentExercise: Exercise? = null,
    val favorites: List<com.example.elite_fitness_app.domain.model.Favorite> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedBodyPart: String = "All",
    val bodyParts: List<String> = listOf("All",
    "Chest",
    "Back",
    "Shoulders",
    "Biceps",
    "Triceps",
    "Forearms",
    "Abs",
    "Obliques",
    "Lower Back",
    "Glutes",
    "Quadriceps",
    "Hamstrings",
    "Calves",
    "Traps",
    "Neck",
    "Cardio",
    "Full Body"),
    val currentPage: Int = 1,
    val isEndReached: Boolean = false
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQueryFlow = MutableStateFlow("")

    init {
        loadExercises()
        loadFavorites()
        setupSearchDebounce()
    }

    fun loadExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPage = 1, isEndReached = false) }
            val selected = _uiState.value.selectedBodyPart
            val result = if (selected == "All") {
                exerciseRepository.getExercises(page = 1, limit = 20)
            } else {
                exerciseRepository.getExercisesByBodyPart(selected)
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            exercises = result.data,
                            isEndReached = if (selected == "All") result.data.size < 20 else true
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

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isPaginatedLoading || currentState.isEndReached || currentState.selectedBodyPart != "All" || currentState.searchQuery.isNotEmpty()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPaginatedLoading = true, error = null) }
            val nextPage = currentState.currentPage + 1
            val result = exerciseRepository.getExercises(page = nextPage, limit = 20)

            when (result) {
                is Resource.Success -> {
                    val newExercises = result.data
                    _uiState.update {
                        it.copy(
                            isPaginatedLoading = false,
                            exercises = it.exercises + newExercises,
                            currentPage = nextPage,
                            isEndReached = newExercises.size < 20
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isPaginatedLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadExerciseById(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = exerciseRepository.getExerciseById(id)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, currentExercise = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun selectBodyPart(bodyPart: String) {
        _uiState.update { it.copy(selectedBodyPart = bodyPart, searchQuery = "") }
        loadExercises()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQueryFlow.value = query
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else if (query.isEmpty() && _uiState.value.selectedBodyPart == "All") {
                        loadExercises()
                    }
                }
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null, currentPage = 1, isEndReached = true) }
        when (val result = exerciseRepository.searchExercises(query)) {
            is Resource.Success -> {
                _uiState.update { it.copy(isLoading = false, exercises = result.data) }
            }
            is Resource.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
            is Resource.Loading -> {}
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

    fun toggleFavorite(exerciseId: String) {
        val currentFavorites = _uiState.value.favorites
        val existingFavorite = currentFavorites.find { it.itemId == exerciseId && it.type == "exercise" }
        
        viewModelScope.launch {
            if (existingFavorite != null) {
                // Already favorited, remove it
                when (val result = exerciseRepository.removeFavorite(existingFavorite.id)) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(favorites = it.favorites.filterNot { fav -> fav.id == existingFavorite.id }) 
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            } else {
                // Not favorited yet, add it
                when (val result = exerciseRepository.addFavorite(exerciseId, "exercise")) {
                    is Resource.Success -> {
                        _uiState.update { 
                            it.copy(favorites = it.favorites + result.data) 
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
