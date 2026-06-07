package com.example.elite_fitness_app.domain.repository

import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.AuthResponse
import com.example.elite_fitness_app.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<AuthResponse>
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        height: Double?,
        weight: Double?,
        fitnessGoal: String?,
        age: Int?
    ): Resource<AuthResponse>
    suspend fun refreshToken(refreshToken: String): Resource<AuthResponse>
    suspend fun logout(): Resource<Unit>
    suspend fun isLoggedIn(): Boolean
}
