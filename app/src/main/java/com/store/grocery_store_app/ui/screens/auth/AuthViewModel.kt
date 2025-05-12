package com.store.grocery_store_app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.local.TokenManager
import com.store.grocery_store_app.data.repository.AuthRepository
import com.store.grocery_store_app.data.repository.impl.SharedUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null,
    val userName: String? = null,
    val userImage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val sharedUserRepository: SharedUserRepository // Add this
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkLoginStatus()
        // Listen for user updates
        viewModelScope.launch {
            sharedUserRepository.userUpdatedFlow.collect {
                // Refresh user info from TokenManager
                refreshUserInfoFromTokenManager()
            }
        }
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val accessToken = tokenManager.accessToken.first()
            val userEmail = tokenManager.userEmail.first()
            val userName = tokenManager.userName.first()
            val userImage = tokenManager.userImage.first()

            _authState.value = AuthState(
                isLoggedIn = !accessToken.isNullOrEmpty(),
                userEmail = userEmail,
                userName = userName,
                userImage = userImage
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState(isLoggedIn = false)
        }
    }

    private suspend fun refreshUserInfoFromTokenManager() {
        _authState.update { currentState ->
            currentState.copy(
                userEmail = tokenManager.userEmail.first(),
                userName = tokenManager.userName.first(),
                userImage = tokenManager.userImage.first()
            )
        }
    }
}