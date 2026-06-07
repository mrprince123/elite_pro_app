package com.example.elite_fitness_app.data.remote

import com.example.elite_fitness_app.data.dto.*
import retrofit2.http.*

/**
 * Retrofit API service for all backend endpoints.
 */
interface ApiService {

    // ─── Auth ─────────────────────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponseDto>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponseDto>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthResponseDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    // ─── User Profile ─────────────────────────────────────────────────────
    @GET("user/profile")
    suspend fun getProfile(): ApiResponse<UserDto>

    @PUT("user/profile")
    suspend fun updateProfile(@Body user: UserDto): ApiResponse<UserDto>

    @Multipart
    @POST("user/profile/image")
    suspend fun uploadProfileImage(
        @Part image: okhttp3.MultipartBody.Part
    ): ApiResponse<UserDto>

    @PATCH("user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): ApiResponse<Unit>

    @DELETE("user")
    suspend fun deleteAccount(): ApiResponse<Unit>

    @Multipart
    @POST("workouts/{id}/image")
    suspend fun uploadWorkoutImage(
        @Path("id") id: String,
        @Part image: okhttp3.MultipartBody.Part
    ): ApiResponse<WorkoutDto>

    @Multipart
    @POST("plans/{id}/image")
    suspend fun uploadPlanImage(
        @Path("id") id: String,
        @Part image: okhttp3.MultipartBody.Part
    ): ApiResponse<TrainingPlanDto>

    // ─── Exercises ────────────────────────────────────────────────────────
    @GET("exercises")
    suspend fun getExercises(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<ExerciseDto>>

    @GET("exercises/{id}")
    suspend fun getExerciseById(@Path("id") id: String): ApiResponse<ExerciseDto>

    @GET("exercises/bodypart/{name}")
    suspend fun getExercisesByBodyPart(@Path("name") bodyPart: String): ApiResponse<List<ExerciseDto>>

    @GET("exercises/target/{name}")
    suspend fun getExercisesByTarget(@Path("name") target: String): ApiResponse<List<ExerciseDto>>

    @GET("exercises/equipment/{name}")
    suspend fun getExercisesByEquipment(@Path("name") equipment: String): ApiResponse<List<ExerciseDto>>

    @GET("exercises/search")
    suspend fun searchExercises(@Query("q") query: String): ApiResponse<List<ExerciseDto>>

    // ─── Workouts ─────────────────────────────────────────────────────────
    @GET("workouts")
    suspend fun getWorkouts(): ApiResponse<List<WorkoutDto>>

    @GET("workouts/{id}")
    suspend fun getWorkoutById(@Path("id") id: String): ApiResponse<WorkoutDto>

    @POST("workouts")
    suspend fun createWorkout(@Body request: CreateWorkoutRequest): ApiResponse<WorkoutDto>

    @PUT("workouts/{id}")
    suspend fun updateWorkout(@Path("id") id: String, @Body request: CreateWorkoutRequest): ApiResponse<WorkoutDto>

    @DELETE("workouts/{id}")
    suspend fun deleteWorkout(@Path("id") id: String): ApiResponse<Unit>

    // ─── Training Plans ───────────────────────────────────────────────────
    @GET("plans")
    suspend fun getPlans(): ApiResponse<List<TrainingPlanDto>>

    @GET("plans/{id}")
    suspend fun getPlanById(@Path("id") id: String): ApiResponse<TrainingPlanDto>

    @GET("plans/bodypart/{name}")
    suspend fun getPlansByBodyPart(@Path("name") bodyPart: String): ApiResponse<List<TrainingPlanDto>>

    // ─── Sessions ─────────────────────────────────────────────────────────
    @POST("sessions/start")
    suspend fun startSession(@Body request: StartSessionRequest): ApiResponse<WorkoutSessionDto>

    @POST("sessions/{id}/end")
    suspend fun endSession(
        @Path("id") sessionId: String,
        @Body request: EndSessionRequest
    ): ApiResponse<WorkoutSessionDto>

    @GET("sessions/history")
    suspend fun getSessionHistory(): ApiResponse<List<WorkoutSessionDto>>

    @GET("sessions/stats")
    suspend fun getSessionStats(): ApiResponse<Map<String, Any>>

    // ─── Favorites ────────────────────────────────────────────────────────
    @POST("favorites")
    suspend fun addFavorite(@Body request: CreateFavoriteRequest): ApiResponse<FavoriteDto>

    @GET("favorites")
    suspend fun getFavorites(): ApiResponse<List<FavoriteDto>>

    @DELETE("favorites/{id}")
    suspend fun removeFavorite(@Path("id") id: String): ApiResponse<Unit>

    // ─── Search ───────────────────────────────────────────────────────────
    @GET("search")
    suspend fun globalSearch(@Query("q") query: String): ApiResponse<Map<String, Any>>

    // ─── Notifications ────────────────────────────────────────────────────
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): ApiResponse<List<NotificationDto>>
}
