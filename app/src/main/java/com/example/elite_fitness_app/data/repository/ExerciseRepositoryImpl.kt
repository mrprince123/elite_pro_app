package com.example.elite_fitness_app.data.repository

import com.example.elite_fitness_app.core.network.safeApiCall
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.data.mapper.toDomain
import com.example.elite_fitness_app.data.remote.ApiService
import com.example.elite_fitness_app.domain.model.Exercise
import com.example.elite_fitness_app.domain.repository.ExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : ExerciseRepository {

    override suspend fun getExercises(page: Int, limit: Int): Resource<List<Exercise>> {
        return when (val result = safeApiCall { api.getExercises(page, limit) }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getExerciseById(id: String): Resource<Exercise> {
        return when (val result = safeApiCall { api.getExerciseById(id) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getExercisesByBodyPart(bodyPart: String): Resource<List<Exercise>> {
        return when (val result = safeApiCall { api.getExercisesByBodyPart(bodyPart) }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getExercisesByTarget(target: String): Resource<List<Exercise>> {
        return when (val result = safeApiCall { api.getExercisesByTarget(target) }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getExercisesByEquipment(equipment: String): Resource<List<Exercise>> {
        return when (val result = safeApiCall { api.getExercisesByEquipment(equipment) }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun searchExercises(query: String): Resource<List<Exercise>> {
        return when (val result = safeApiCall { api.searchExercises(query) }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
