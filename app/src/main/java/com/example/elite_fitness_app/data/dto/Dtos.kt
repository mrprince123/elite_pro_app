package com.example.elite_fitness_app.data.dto

import com.google.gson.annotations.SerializedName

// ─── API Response Wrapper ─────────────────────────────────────────────────
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String = "",
    @SerializedName("data") val data: T? = null,
)

// ─── Auth DTOs ────────────────────────────────────────────────────────────
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("height") val height: Double?,
    @SerializedName("weight") val weight: Double?,
    @SerializedName("fitnessGoal") val fitnessGoal: String?,
    @SerializedName("age") val age: Int?,
)

data class AuthResponseDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("user") val user: UserDto,
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String,
)

// ─── User DTOs ────────────────────────────────────────────────────────────
data class UserDto(
    @SerializedName("_id") val id: String? = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("email") val email: String? = "",
    @SerializedName("phone") val phone: String? = "",
    @SerializedName("age") val age: Int? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("height") val height: Double? = null,
    @SerializedName("weight") val weight: Double? = null,
    @SerializedName("fitnessGoal") val fitnessGoal: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("role") val role: String? = "user",
    @SerializedName("isVerified") val isVerified: Boolean? = false,
)

// ─── Exercise DTOs ────────────────────────────────────────────────────────
data class ExerciseDto(
    @SerializedName("_id") val id: String? = "",
    @SerializedName("externalId") val externalId: String? = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("bodyPart") val bodyPart: String? = "",
    @SerializedName("target") val target: String? = "",
    @SerializedName("equipment") val equipment: String? = "",
    @SerializedName("gifUrl") val gifUrl: String? = "",
    @SerializedName("videoUrl") val videoUrl: String? = "",
    @SerializedName("instructions") val instructions: List<String>? = emptyList(),
    @SerializedName("secondaryMuscles") val secondaryMuscles: List<String>? = emptyList(),
)

// ─── Workout DTOs ─────────────────────────────────────────────────────────
data class WorkoutDto(
    @SerializedName("_id") val id: String? = "",
    @SerializedName("userId") val userId: String? = "",
    @SerializedName("name") val name: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("difficulty") val difficulty: String? = "",
    @SerializedName("estimatedDuration") val estimatedDuration: Int? = 0,
    @SerializedName("exercises") val exercises: List<WorkoutExerciseDto>? = emptyList(),
    @SerializedName("isFavorite") val isFavorite: Boolean? = false,
    @SerializedName("createdAt") val createdAt: String? = "",
    @SerializedName("image") val image: String? = null,
)

data class WorkoutExerciseDto(
    @SerializedName("exerciseId") val exercise: ExerciseDto? = null,
    @SerializedName("exerciseName") val exerciseName: String? = "",
    @SerializedName("sets") val sets: Int? = 0,
    @SerializedName("reps") val reps: Int? = 0,
    @SerializedName("restTime") val restTime: Int? = 0,
    @SerializedName("order") val order: Int? = 0,
)

data class WorkoutExerciseRequestDto(
    @SerializedName("exerciseId") val exerciseId: String,
    @SerializedName("exerciseName") val exerciseName: String?,
    @SerializedName("sets") val sets: Int,
    @SerializedName("reps") val reps: Int,
    @SerializedName("restTime") val restTime: Int,
    @SerializedName("order") val order: Int,
)

data class CreateWorkoutRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("estimatedDuration") val estimatedDuration: Int,
    @SerializedName("exercises") val exercises: List<WorkoutExerciseRequestDto>,
)

// ─── Training Plan DTOs ───────────────────────────────────────────────────
data class TrainingPlanDto(
    @SerializedName("_id") val id: String? = "",
    @SerializedName("title") val title: String? = "",
    @SerializedName("bodyPart") val bodyPart: String? = "",
    @SerializedName("level") val level: String? = "",
    @SerializedName("duration") val duration: Int? = 0,
    @SerializedName("caloriesEstimate") val caloriesEstimate: Int? = 0,
    @SerializedName("exercises") val exercises: List<ExerciseDto>? = emptyList(),
    @SerializedName("thumbnail") val thumbnail: String? = "",
)

// ─── Session DTOs ─────────────────────────────────────────────────────────
data class WorkoutSessionDto(
    @SerializedName("_id") val id: String? = "",
    @SerializedName("userId") val userId: String? = "",
    @SerializedName("workoutId") val workout: WorkoutDto? = null,
    @SerializedName("startedAt") val startedAt: String? = "",
    @SerializedName("endedAt") val endedAt: String? = null,
    @SerializedName("caloriesBurned") val caloriesBurned: Int? = 0,
    @SerializedName("totalDuration") val totalDuration: Int? = 0,
    @SerializedName("completedExercises") val completedExercises: Int? = 0,
    @SerializedName("notes") val notes: String? = "",
)

data class StartSessionRequest(
    @SerializedName("workoutId") val workoutId: String,
)

data class EndSessionRequest(
    @SerializedName("caloriesBurned") val caloriesBurned: Int,
    @SerializedName("completedExercises") val completedExercises: Int,
    @SerializedName("notes") val notes: String,
)

// ─── Favorite DTOs ────────────────────────────────────────────────────────
data class FavoriteDto(
    @SerializedName("_id") val id: String? = "",
    @SerializedName("userId") val userId: String? = "",
    @SerializedName("type") val type: String? = "",       // "exercise" | "workout"
    @SerializedName("itemId") val itemId: String? = "",
)

data class CreateFavoriteRequest(
    @SerializedName("type") val type: String,
    @SerializedName("itemId") val itemId: String,
)

// ─── Password DTOs ────────────────────────────────────────────────────────
data class ChangePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String,
)

// ─── Notification DTOs ────────────────────────────────────────────────────
data class NotificationDto(
    @SerializedName("_id") val id: String? = "",
    @SerializedName("userId") val userId: String? = "",
    @SerializedName("title") val title: String? = "",
    @SerializedName("body") val body: String? = "",
    @SerializedName("type") val type: String? = "",
    @SerializedName("isRead") val isRead: Boolean? = false,
    @SerializedName("createdAt") val createdAt: String? = "",
)
