package com.example.elite_fitness_app.domain.repository

import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Resource<List<Notification>>
}
