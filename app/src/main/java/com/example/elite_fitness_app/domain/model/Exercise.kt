package com.example.elite_fitness_app.domain.model

data class Exercise(
    val id: String = "",
    val externalId: String = "",
    val name: String = "",
    val bodyPart: String = "",
    val target: String = "",
    val equipment: String = "",
    val gifUrl: String = "",
    val videoUrl: String = "",
    val instructions: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
)
