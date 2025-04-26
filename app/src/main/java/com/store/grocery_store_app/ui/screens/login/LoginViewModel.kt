package com.store.grocery_store_app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.response.AuthResponse
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
data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: AuthResponse? = null,
    val error: String? = null
)
@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel()
{
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, emailError = "") }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, passwordError = "") }
    }

    fun login() {
        val email = state.value.email
        val password = state.value.password

        // Validate
        var isValid = true

        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Email không được để trống") }
            isValid = false
        } else if (!ValidationUtils.isValidEmail(email)) {
            _state.update { it.copy(emailError = "Email không hợp lệ") }
            isValid = false
        }

        if (password.isBlank()) {
            _state.update { it.copy(passwordError = "Mật khẩu không được để trống") }
            isValid = false
        } else if (!ValidationUtils.isValidPassword(password)) {
            _state.update { it.copy(passwordError = "Mật khẩu phải có ít nhất 6 ký tự") }
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                loginSuccess = result.data,
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
}