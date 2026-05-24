package com.example.elite_fitness_app.domain.repository

import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.WorkoutSession

interface SessionRepository {
    suspend fun startSession(workoutId: String): Resource<WorkoutSession>
    suspend fun endSession(sessionId: String, caloriesBurned: Int, completedExercises: Int, notes: String): Resource<WorkoutSession>
    suspend fun getHistory(): Resource<List<WorkoutSession>>
    suspend fun getStats(): Resource<Map<String, Any>>
}
