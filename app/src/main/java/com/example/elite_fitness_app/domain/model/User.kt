package com.example.elite_fitness_app.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val age: Int? = null,
    val gender: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val fitnessGoal: String? = null,
    val profileImage: String? = null,
    val role: String = "user",
    val isVerified: Boolean = false,
)
