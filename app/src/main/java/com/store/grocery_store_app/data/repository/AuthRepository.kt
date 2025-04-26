package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.request.RegisterRequest
import com.store.grocery_store_app.data.models.response.AuthResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<Resource<AuthResponse>>
    suspend fun register(registerRequest: RegisterRequest): Flow<Resource<Boolean>>
    suspend fun forgotPassword(email: String, newPassword: String): Flow<Resource<Boolean>>
    suspend fun sendOtp(email: String, forRegistration: Boolean): Flow<Resource<Boolean>>
    suspend fun verifyOtp(otp: String): Flow<Resource<Boolean>>
    suspend fun refreshToken(refreshToken: String): Flow<Resource<String>>
    suspend fun logout()
}