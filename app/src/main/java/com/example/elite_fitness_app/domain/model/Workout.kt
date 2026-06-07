package com.example.elite_fitness_app.domain.model

data class Workout(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val estimatedDuration: Int = 0,
    val exercises: List<WorkoutExercise> = emptyList(),
    val isFavorite: Boolean = false,
    val createdAt: String = "",
    val image: String? = null,
)

data class WorkoutExercise(
    val exerciseId: String = "",
    val exerciseName: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val restTime: Int = 0,
    val order: Int = 0,
)
