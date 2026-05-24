package com.example.elite_fitness_app.domain.model

data class WorkoutSession(
    val id: String = "",
    val userId: String = "",
    val workoutId: String = "",
    val startedAt: String = "",
    val endedAt: String? = null,
    val caloriesBurned: Int = 0,
    val totalDuration: Int = 0,
    val completedExercises: Int = 0,
    val notes: String = "",
)
