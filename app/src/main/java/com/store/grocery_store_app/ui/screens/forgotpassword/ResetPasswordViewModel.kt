package com.store.grocery_store_app.ui.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class ResetPasswordState(
    val email: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val newPasswordError: String = "",
    val confirmNewPasswordError: String = "",
    val isLoading: Boolean = false,
    val isReset: Boolean = false,
    val error: String? = null
)
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()
{
    private val _state = MutableStateFlow(ResetPasswordState())
    val state: StateFlow<ResetPasswordState> = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.update { it.copy(email = email) }
    }
    fun onNewPasswordChange(newPassword: String) {
        _state.update {
            it.copy(
                newPassword = newPassword,
                newPasswordError = "",
                confirmNewPasswordError = if (it.confirmNewPassword.isNotEmpty() &&
                    !ValidationUtils.doPasswordsMatch(newPassword, it.confirmNewPassword))
                    "Mật khẩu xác nhận không khớp"
                else
                    ""
            )
        }
    }

    fun onConfirmNewPasswordChange(confirmNewPassword: String) {
        _state.update {
            it.copy(
                confirmNewPassword = confirmNewPassword,
                confirmNewPasswordError = if (!ValidationUtils.doPasswordsMatch(it.newPassword, confirmNewPassword))
                    "Mật khẩu xác nhận không khớp"
                else
                    ""
            )
        }
    }

    /**
     * Xử lý đặt lại mật khẩu
     */
    fun resetPassword() {
        val currentState = state.value

        // Validate fields
        var isValid = true
        var newPasswordError = ""
        var confirmNewPasswordError = ""

        if (currentState.newPassword.isBlank()) {
            newPasswordError = "Mật khẩu mới không được để trống"
            isValid = false
        } else if (!ValidationUtils.isValidPassword(currentState.newPassword)) {
            newPasswordError = "Mật khẩu mới phải có ít nhất 6 ký tự"
            isValid = false
        }

        if (currentState.confirmNewPassword.isBlank()) {
            confirmNewPasswordError = "Vui lòng xác nhận mật khẩu mới"
            isValid = false
        } else if (!ValidationUtils.doPasswordsMatch(currentState.newPassword, currentState.confirmNewPassword)) {
            confirmNewPasswordError = "Mật khẩu xác nhận không khớp"
            isValid = false
        }

        if (!isValid) {
            _state.update { it.copy(
                newPasswordError = newPasswordError,
                confirmNewPasswordError = confirmNewPasswordError
            ) }
            return
        }

        viewModelScope.launch {
            authRepository.forgotPassword(currentState.email, currentState.newPassword).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isReset = true,
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