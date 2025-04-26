package com.store.grocery_store_app.data.api

import com.store.grocery_store_app.data.models.request.AuthRequest
import com.store.grocery_store_app.data.models.request.ForgotPasswordRequest
import com.store.grocery_store_app.data.models.request.OtpRequest
import com.store.grocery_store_app.data.models.request.OtpVerifyRequest
import com.store.grocery_store_app.data.models.request.RegisterRequest
import com.store.grocery_store_app.data.models.response.ApiResponse
import com.store.grocery_store_app.data.models.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Any>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Any>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Query("refreshToken") refreshToken: String): Response<ApiResponse<String>>

    @POST("otp/send")
    suspend fun sendOtp(@Body request: OtpRequest): Response<ApiResponse<Any>>

    @POST("otp/verify")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): Response<ApiResponse<Any>>
}