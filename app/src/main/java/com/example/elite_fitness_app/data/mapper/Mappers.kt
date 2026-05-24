package com.example.elite_fitness_app.data.mapper

import com.example.elite_fitness_app.data.dto.*
import com.example.elite_fitness_app.domain.model.*

// ─── User Mappers ─────────────────────────────────────────────────────────
fun UserDto.toDomain() = User(
    id = id ?: "",
    name = name ?: "",
    email = email ?: "",
    phone = phone ?: "",
    age = age,
    gender = gender,
    height = height,
    weight = weight,
    fitnessGoal = fitnessGoal,
    profileImage = profileImage,
    role = role ?: "user",
    isVerified = isVerified ?: false,
)

fun AuthResponseDto.toDomain() = AuthResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    user = user.toDomain(),
)

// ─── Exercise Mappers ─────────────────────────────────────────────────────
fun ExerciseDto.toDomain() = Exercise(
    id = id ?: "",
    externalId = externalId ?: "",
    name = name ?: "",
    bodyPart = bodyPart ?: "",
    target = target ?: "",
    equipment = equipment ?: "",
    gifUrl = gifUrl ?: "",
    videoUrl = videoUrl ?: "",
    instructions = instructions ?: emptyList(),
    secondaryMuscles = secondaryMuscles ?: emptyList(),
)

// ─── Workout Mappers ──────────────────────────────────────────────────────
fun WorkoutExerciseDto.toDomain() = WorkoutExercise(
    exerciseId = exercise?.id ?: "",
    exerciseName = exercise?.name ?: exerciseName ?: "",
    sets = sets ?: 0,
    reps = reps ?: 0,
    restTime = restTime ?: 0,
    order = order ?: 0,
)

fun WorkoutDto.toDomain() = Workout(
    id = id ?: "",
    userId = userId ?: "",
    name = name ?: "",
    description = description ?: "",
    difficulty = difficulty ?: "",
    estimatedDuration = estimatedDuration ?: 0,
    exercises = exercises?.map { it.toDomain() } ?: emptyList(),
    isFavorite = isFavorite ?: false,
    createdAt = createdAt ?: "",
)

fun Workout.toCreateRequest() = CreateWorkoutRequest(
    name = name, description = description,
    difficulty = difficulty, estimatedDuration = estimatedDuration,
    exercises = exercises.map {
        WorkoutExerciseRequestDto(
            exerciseId = it.exerciseId, exerciseName = it.exerciseName,
            sets = it.sets, reps = it.reps,
            restTime = it.restTime, order = it.order,
        )
    },
)

// ─── TrainingPlan Mappers ─────────────────────────────────────────────────
fun TrainingPlanDto.toDomain() = TrainingPlan(
    id = id ?: "",
    title = title ?: "",
    bodyPart = bodyPart ?: "",
    level = level ?: "",
    duration = duration ?: 0,
    caloriesEstimate = caloriesEstimate ?: 0,
    exercises = exercises?.map { it.toDomain() } ?: emptyList(),
    thumbnail = thumbnail ?: "",
)

// ─── Session Mappers ──────────────────────────────────────────────────────
fun WorkoutSessionDto.toDomain() = WorkoutSession(
    id = id ?: "",
    userId = userId ?: "",
    workoutId = workout?.id ?: "",
    startedAt = startedAt ?: "",
    endedAt = endedAt,
    caloriesBurned = caloriesBurned ?: 0,
    totalDuration = totalDuration ?: 0,
    completedExercises = completedExercises ?: 0,
    notes = notes ?: "",
)
