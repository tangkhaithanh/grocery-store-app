package com.store.grocery_store_app.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.request.RegisterRequest
import com.store.grocery_store_app.data.repository.AuthRepository
import com.store.grocery_store_app.utils.Resource
import com.store.grocery_store_app.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val fullName: String = "",
    val phone: String = "",
    val gender: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String = "",
    val phoneError: String = "",
    val genderError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
)
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onFullNameChange(fullName: String) {
        _state.update { it.copy(fullName = fullName, fullNameError = "") }
    }

    fun onPhoneChange(phone: String) {
        _state.update { it.copy(phone = phone, phoneError = "") }
    }

    fun onGenderChange(gender: String) {
        _state.update { it.copy(gender = gender, genderError = "") }
    }

    fun onPasswordChange(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordError = "",
                confirmPasswordError = if (it.confirmPassword.isNotEmpty() &&
                    !ValidationUtils.doPasswordsMatch(password, it.confirmPassword)
                )
                    "Mật khẩu xác nhận không khớp"
                else
                    ""
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (!ValidationUtils.doPasswordsMatch(it.password, confirmPassword))
                    "Mật khẩu xác nhận không khớp"
                else
                    ""
            )
        }
    }
    /**
     * Xử lý đăng ký tài khoản
     */
    fun register() {
        val currentState = state.value

        // Validate fields
        var isValid = true
        var fullNameError = ""
        var phoneError = ""
        var genderError = ""
        var passwordError = ""
        var confirmPasswordError = ""

        if (currentState.fullName.isBlank()) {
            fullNameError = "Họ và tên không được để trống"
            isValid = false
        } else if (!ValidationUtils.isValidName(currentState.fullName)) {
            fullNameError = "Họ và tên không hợp lệ"
            isValid = false
        }

        if (currentState.phone.isBlank()) {
            phoneError = "Số điện thoại không được để trống"
            isValid = false
        } else if (!ValidationUtils.isValidPhoneNumber(currentState.phone)) {
            phoneError = "Số điện thoại không hợp lệ"
            isValid = false
        }

        if (currentState.gender.isBlank()) {
            genderError = "Vui lòng chọn giới tính"
            isValid = false
        }

        if (currentState.password.isBlank()) {
            passwordError = "Mật khẩu không được để trống"
            isValid = false
        } else if (!ValidationUtils.isValidPassword(currentState.password)) {
            passwordError = "Mật khẩu phải có ít nhất 6 ký tự"
            isValid = false
        }

        if (currentState.confirmPassword.isBlank()) {
            confirmPasswordError = "Vui lòng xác nhận mật khẩu"
            isValid = false
        } else if (!ValidationUtils.doPasswordsMatch(currentState.password, currentState.confirmPassword)) {
            confirmPasswordError = "Mật khẩu xác nhận không khớp"
            isValid = false
        }

        if (!isValid) {
            _state.update { it.copy(
                fullNameError = fullNameError,
                phoneError = phoneError,
                genderError = genderError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            ) }
            return
        }

        // Create register request
        val registerRequest = RegisterRequest(
            fullName = currentState.fullName,
            phone = currentState.phone,
            gender = currentState.gender,
            email = currentState.email,
            password = currentState.password
        )

        viewModelScope.launch {
            authRepository.register(registerRequest).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRegistered = true,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Xóa thông báo lỗi
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}