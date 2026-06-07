package com.example.elite_fitness_app.data.repository

import com.example.elite_fitness_app.core.network.safeApiCall
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.data.dto.ChangePasswordRequest
import com.example.elite_fitness_app.data.mapper.toDomain
import com.example.elite_fitness_app.data.remote.ApiService
import com.example.elite_fitness_app.domain.model.User
import com.example.elite_fitness_app.domain.repository.UserRepository
import com.example.elite_fitness_app.data.dto.UserDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : UserRepository {

    override suspend fun getProfile(): Resource<User> {
        return when (val result = safeApiCall { api.getProfile() }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateProfile(user: User): Resource<User> {
        val dto = UserDto(
            id = user.id, name = user.name, email = user.email,
            phone = user.phone, age = user.age, gender = user.gender,
            height = user.height, weight = user.weight,
            fitnessGoal = user.fitnessGoal, profileImage = user.profileImage,
        )
        return when (val result = safeApiCall { api.updateProfile(dto) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Resource<Unit> {
        return when (val result = safeApiCall { api.changePassword(ChangePasswordRequest(oldPassword, newPassword)) }) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun deleteAccount(): Resource<Unit> {
        return when (val result = safeApiCall { api.deleteAccount() }) {
            is Resource.Success -> Resource.Success(Unit)
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun uploadProfileImage(file: java.io.File): Resource<User> {
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestFile = file.asRequestBody(mediaType)
        val body = okhttp3.MultipartBody.Part.createFormData("image", file.name, requestFile)
        return when (val result = safeApiCall { api.uploadProfileImage(body) }) {
            is Resource.Success -> Resource.Success(result.data.data!!.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
