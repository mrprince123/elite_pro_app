package com.example.elite_fitness_app.data.repository

import com.example.elite_fitness_app.core.network.safeApiCall
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.data.mapper.toDomain
import com.example.elite_fitness_app.data.mapper.toCreateRequest
import com.example.elite_fitness_app.data.remote.ApiService
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.domain.repository.WorkoutRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : WorkoutRepository {

    override suspend fun getWorkouts(): Resource<List<Workout>> {
        return when (val result = safeApiCall { api.getWorkouts() }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getWorkoutById(id: String): Resource<Workout> {
        return when (val result = safeApiCall { api.getWorkoutById(id) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun createWorkout(workout: Workout): Resource<Workout> {
        return when (val result = safeApiCall { api.createWorkout(workout.toCreateRequest()) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateWorkout(id: String, workout: Workout): Resource<Workout> {
        return when (val result = safeApiCall { api.updateWorkout(id, workout.toCreateRequest()) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deleteWorkout(id: String): Resource<Unit> {
        return when (val result = safeApiCall { api.deleteWorkout(id) }) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun uploadWorkoutImage(id: String, file: java.io.File): Resource<Workout> {
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestFile = file.asRequestBody(mediaType)
        val body = okhttp3.MultipartBody.Part.createFormData("image", file.name, requestFile)
        return when (val result = safeApiCall { api.uploadWorkoutImage(id, body) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
