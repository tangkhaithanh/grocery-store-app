package com.store.grocery_store_app.ui.screens.profile
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.local.TokenManager
import com.store.grocery_store_app.data.models.request.UpdateUserRequest
import com.store.grocery_store_app.data.models.response.UserDTO
import com.store.grocery_store_app.data.repository.CloudinaryRepository
import com.store.grocery_store_app.data.repository.UserRepository
import com.store.grocery_store_app.data.repository.impl.SharedUserRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userDetail: UserDTO? = null,
    val error: String? = null,
    val isUploading: Boolean = false,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false
)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cloudinaryRepository: CloudinaryRepository,
    private val tokenManager: TokenManager,
    private val sharedUserRepository: SharedUserRepository // Add this
):ViewModel(){
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    // Lấy thông tin người dùng khi viewModel được gọi:
    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.userId.first()
                if (userId != null) {
                    userRepository.getUserById(userId).collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                _uiState.update { it.copy(isLoading = true, error = null) }
                            }
                            is Resource.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        userDetail = result.data,
                                        error = null
                                    )
                                }
                            }
                            is Resource.Error -> {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = result.message
                                    )
                                }
                            }
                        }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Không tìm thấy ID người dùng") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            cloudinaryRepository.uploadImage(imageUri).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isUploading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isUploading = false,
                                userDetail = result.data?.let { currentState.userDetail?.copy(imageUrl = it) }
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateProfile(
        fullName: String,
        phone: String,
        gender: String
    ) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.userId.first()
                val currentImageUrl = _uiState.value.userDetail?.imageUrl ?: ""

                if (userId != null) {
                    val request = UpdateUserRequest(
                        fullName = fullName,
                        phone = phone,
                        gender = gender,
                        imageUrl = currentImageUrl
                    )

                    userRepository.updateUser(userId, request).collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                _uiState.update { it.copy(isUpdating = true, error = null) }
                            }
                            is Resource.Success -> {
                                // Update TokenManager with new user info
                                result.data?.let { updatedUser ->
                                    val accessToken = tokenManager.accessToken.first() ?: ""
                                    val refreshToken = tokenManager.refreshToken.first() ?: ""

                                    tokenManager.saveUserInfo(
                                        accessToken = accessToken,
                                        refreshToken = refreshToken,
                                        email = updatedUser.email,
                                        fullName = updatedUser.fullName,
                                        imageUrl = updatedUser.imageUrl,
                                        userId = updatedUser.id
                                    )
                                }

                                // Notify other screens that user was updated
                                sharedUserRepository.notifyUserUpdated() // Add this line

                                _uiState.update {
                                    it.copy(
                                        isUpdating = false,
                                        userDetail = result.data,
                                        updateSuccess = true,
                                        error = null
                                    )
                                }
                            }
                            is Resource.Error -> {
                                _uiState.update {
                                    it.copy(
                                        isUpdating = false,
                                        error = result.message
                                    )
                                }
                            }
                        }
                    }
                } else {
                    _uiState.update { it.copy(error = "Không tìm thấy ID người dùng") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearUpdateSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }
}