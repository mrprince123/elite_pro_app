package com.example.elite_fitness_app.domain.repository

import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.Workout

interface WorkoutRepository {
    suspend fun getWorkouts(): Resource<List<Workout>>
    suspend fun getWorkoutById(id: String): Resource<Workout>
    suspend fun createWorkout(workout: Workout): Resource<Workout>
    suspend fun updateWorkout(id: String, workout: Workout): Resource<Workout>
    suspend fun deleteWorkout(id: String): Resource<Unit>
    suspend fun uploadWorkoutImage(id: String, file: java.io.File): Resource<Workout>
}
