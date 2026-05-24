package com.example.elite_fitness_app.domain.repository

import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.User

interface UserRepository {
    suspend fun getProfile(): Resource<User>
    suspend fun updateProfile(user: User): Resource<User>
    suspend fun changePassword(oldPassword: String, newPassword: String): Resource<Unit>
    suspend fun deleteAccount(): Resource<Unit>
}
