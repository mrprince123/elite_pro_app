package com.example.elite_fitness_app.domain.repository

import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.TrainingPlan

interface TrainingPlanRepository {
    suspend fun getPlans(): Resource<List<TrainingPlan>>
    suspend fun getPlanById(id: String): Resource<TrainingPlan>
    suspend fun getPlansByBodyPart(bodyPart: String): Resource<List<TrainingPlan>>
}
