package com.example.elite_fitness_app.domain.model

data class Favorite(
    val id: String,
    val userId: String,
    val type: String,
    val itemId: String
)
