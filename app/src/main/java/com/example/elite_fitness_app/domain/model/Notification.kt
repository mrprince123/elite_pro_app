package com.example.elite_fitness_app.domain.model

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)
