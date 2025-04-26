package com.store.grocery_store_app.ui.screens.EmailVerification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.repository.AuthRepository
import com.store.grocery_store_app.utils.AuthPurpose
import com.store.grocery_store_app.utils.Resource
import com.store.grocery_store_app.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmailVerificationState(
    val purpose: AuthPurpose = AuthPurpose.REGISTRATION,
    val email: String = "",
    val emailError: String = "",
    val isLoading: Boolean = false,
    val otpSent: Boolean = false,
    val error: String? = null
)
@HiltViewModel
class EmailVerificationViewModel @Inject constructor(private val authRepository: AuthRepository):
    ViewModel()
{
    private val _state = MutableStateFlow(EmailVerificationState())
    val state: StateFlow<EmailVerificationState> = _state.asStateFlow()

    fun setPurpose(purpose: AuthPurpose) {
        _state.update { it.copy(purpose = purpose) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, emailError = "") }
    }

    fun sendOtp() {
        val email = state.value.email
        // Validate email
        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Email không được để trống") }
            return
        } else if (!ValidationUtils.isValidEmail(email)) {
            _state.update { it.copy(emailError = "Email không hợp lệ") }
            return
        }

        // Determine if OTP is for registration or password reset
        val forRegistration = state.value.purpose == AuthPurpose.REGISTRATION
        viewModelScope.launch {
            authRepository.sendOtp(email, forRegistration).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                otpSent = true,
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

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetOtpSentState() {
        _state.update { it.copy(otpSent = false) }
    }
}