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
    val exercises: List<Exercise> = emptyList(),
    val currentExercise: Exercise? = null,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedBodyPart: String = "All",
    val bodyParts: List<String> = listOf("All", "chest", "back", "cardio", "lower arms", "lower legs", "neck", "shoulders", "upper arms", "upper legs", "waist")
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
        setupSearchDebounce()
    }

    fun loadExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val selected = _uiState.value.selectedBodyPart
            val result = if (selected == "All") {
                exerciseRepository.getExercises(page = 1, limit = 50)
            } else {
                exerciseRepository.getExercisesByBodyPart(selected)
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, exercises = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
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
        _uiState.update { it.copy(isLoading = true, error = null) }
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
}
