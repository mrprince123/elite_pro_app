package com.example.elite_fitness_app.domain.model

data class AuthResponse(
    val accessToken: String = "",
    val refreshToken: String = "",
    val user: User = User()
)
