package com.example.elite_fitness_app.domain.model

data class TrainingPlan(
    val id: String = "",
    val title: String = "",
    val bodyPart: String = "",
    val level: String = "",
    val duration: Int = 0,
    val caloriesEstimate: Int = 0,
    val exercises: List<Exercise> = emptyList(),
    val thumbnail: String = "",
)
