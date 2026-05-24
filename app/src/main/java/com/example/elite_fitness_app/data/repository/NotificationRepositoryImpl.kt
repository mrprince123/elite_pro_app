package com.example.elite_fitness_app.data.repository

import com.example.elite_fitness_app.core.network.safeApiCall
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.data.mapper.toDomain
import com.example.elite_fitness_app.data.remote.ApiService
import com.example.elite_fitness_app.domain.model.Notification
import com.example.elite_fitness_app.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val api: ApiService
) : NotificationRepository {
    override suspend fun getNotifications(): Resource<List<Notification>> {
        return when (val result = safeApiCall { api.getNotifications() }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
