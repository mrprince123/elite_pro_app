package com.example.elite_fitness_app.data.repository

import com.example.elite_fitness_app.core.network.safeApiCall
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.data.mapper.toDomain
import com.example.elite_fitness_app.data.remote.ApiService
import com.example.elite_fitness_app.domain.model.TrainingPlan
import com.example.elite_fitness_app.domain.repository.TrainingPlanRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingPlanRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : TrainingPlanRepository {

    override suspend fun getPlans(): Resource<List<TrainingPlan>> {
        return when (val result = safeApiCall { api.getPlans() }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getPlanById(id: String): Resource<TrainingPlan> {
        return when (val result = safeApiCall { api.getPlanById(id) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getPlansByBodyPart(bodyPart: String): Resource<List<TrainingPlan>> {
        return when (val result = safeApiCall { api.getPlansByBodyPart(bodyPart) }) {
            is Resource.Success -> Resource.Success(result.data.data?.map { it.toDomain() } ?: emptyList())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
