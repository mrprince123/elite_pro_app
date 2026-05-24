package com.example.elite_fitness_app.data.repository

import com.example.elite_fitness_app.core.network.safeApiCall
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.data.dto.EndSessionRequest
import com.example.elite_fitness_app.data.dto.StartSessionRequest
import com.example.elite_fitness_app.data.mapper.toDomain
import com.example.elite_fitness_app.data.remote.ApiService
import com.example.elite_fitness_app.domain.model.WorkoutSession
import com.example.elite_fitness_app.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : SessionRepository {

    override suspend fun startSession(workoutId: String): Resource<WorkoutSession> {
        return when (val result = safeApiCall { api.startSession(StartSessionRequest(workoutId)) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun endSession(
        sessionId: String, caloriesBurned: Int, completedExercises: Int, notes: String
    ): Resource<WorkoutSession> {
        return when (val result = safeApiCall {
            api.endSession(sessionId, EndSessionRequest(caloriesBurned, completedExercises, notes))
        }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getHistory(): Resource<List<WorkoutSession>> {
        return when (val result = safeApiCall { api.getSessionHistory() }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getStats(): Resource<Map<String, Any>> {
        return when (val result = safeApiCall { api.getSessionStats() }) {
            is Resource.Success -> Resource.Success(result.data.data ?: emptyMap())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
