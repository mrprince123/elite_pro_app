package com.example.elite_fitness_app.domain.repository

import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.Exercise

interface ExerciseRepository {
    suspend fun getExercises(page: Int, limit: Int): Resource<List<Exercise>>
    suspend fun getExerciseById(id: String): Resource<Exercise>
    suspend fun getExercisesByBodyPart(bodyPart: String): Resource<List<Exercise>>
    suspend fun getExercisesByTarget(target: String): Resource<List<Exercise>>
    suspend fun getExercisesByEquipment(equipment: String): Resource<List<Exercise>>
    suspend fun searchExercises(query: String): Resource<List<Exercise>>
}
