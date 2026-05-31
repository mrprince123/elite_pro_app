package com.example.elite_fitness_app.presentation.account

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elite_fitness_app.core.utils.Resource
import com.example.elite_fitness_app.domain.model.User
import com.example.elite_fitness_app.domain.repository.AuthRepository
import com.example.elite_fitness_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class AccountUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val success: Boolean = false,
    val editName: String = "",
    val editPhone: String = "",
    val editHeight: String = "",
    val editWeight: String = "",
    val editGoal: String = "",
    val isLoggedOut: Boolean = false,
    val isEditing: Boolean = false,
    val profileImageUri: String? = null // Local file URI for display
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = userRepository.getProfile()) {
                is Resource.Success -> {
                    val u = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = u,
                            editName = u.name,
                            editPhone = u.phone,
                            editHeight = u.height?.toString() ?: "",
                            editWeight = u.weight?.toString() ?: "",
                            editGoal = u.fitnessGoal ?: "",
                            profileImageUri = u.profileImage
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun updateName(name: String) = _uiState.update { it.copy(editName = name) }
    fun updatePhone(phone: String) = _uiState.update { it.copy(editPhone = phone) }
    fun updateHeight(h: String) = _uiState.update { it.copy(editHeight = h) }
    fun updateWeight(w: String) = _uiState.update { it.copy(editWeight = w) }
    fun updateGoal(g: String) = _uiState.update { it.copy(editGoal = g) }

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditing = !it.isEditing, success = false, error = null) }
    }

    fun cancelEdit() {
        val user = _uiState.value.user
        _uiState.update {
            it.copy(
                isEditing = false,
                success = false,
                error = null,
                editName = user?.name ?: "",
                editPhone = user?.phone ?: "",
                editHeight = user?.height?.toString() ?: "",
                editWeight = user?.weight?.toString() ?: "",
                editGoal = user?.fitnessGoal ?: "",
                profileImageUri = user?.profileImage
            )
        }
    }

    /**
     * Process a selected image URI: copy it to local storage and convert to base64.
     */
    fun onProfileImageSelected(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                // Scale down if too large (max 256x256 for profile pic)
                val scaledBitmap = scaleBitmap(bitmap, 256)

                // Save to local storage
                val localFile = File(context.filesDir, "profile_picture.jpg")
                FileOutputStream(localFile).use { out ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }

                // Convert to base64 for API upload
                val baos = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                val base64String = "data:image/jpeg;base64," +
                        Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)

                _uiState.update {
                    it.copy(
                        profileImageUri = localFile.toURI().toString()
                    )
                }

                // Update the user model's profileImage with base64 for server sync
                val currentUser = _uiState.value.user ?: return@launch
                _uiState.update {
                    it.copy(
                        user = currentUser.copy(profileImage = base64String)
                    )
                }

                Timber.d("Profile image processed and saved locally")
            } catch (e: Exception) {
                Timber.e(e, "Failed to process profile image")
                _uiState.update { it.copy(error = "Failed to process image") }
            }
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val ratio = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
        if (ratio >= 1f) return bitmap
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun saveProfile() {
        val state = _uiState.value
        val currentUser = state.user ?: return
        val updatedUser = currentUser.copy(
            name = state.editName,
            phone = state.editPhone,
            height = state.editHeight.toDoubleOrNull(),
            weight = state.editWeight.toDoubleOrNull(),
            fitnessGoal = state.editGoal
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }
            when (val result = userRepository.updateProfile(updatedUser)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = result.data,
                            success = true,
                            isEditing = false // Exit edit mode on success
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.logout()
            _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
            onSuccess()
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = userRepository.deleteAccount()
            if (result is Resource.Success) {
                authRepository.logout()
                _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
                onSuccess()
            } else if (result is Resource.Error) {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
