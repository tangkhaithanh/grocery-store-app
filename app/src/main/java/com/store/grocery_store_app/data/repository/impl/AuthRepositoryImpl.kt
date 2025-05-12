package com.store.grocery_store_app.data.repository.impl
import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.local.TokenManager
import com.store.grocery_store_app.data.models.request.AuthRequest
import com.store.grocery_store_app.data.models.request.ForgotPasswordRequest
import com.store.grocery_store_app.data.models.request.OtpRequest
import com.store.grocery_store_app.data.models.request.OtpVerifyRequest
import com.store.grocery_store_app.data.models.request.RegisterRequest
import com.store.grocery_store_app.data.models.response.AuthResponse
import com.store.grocery_store_app.data.repository.AuthRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val apiService: ApiService,
                                             private val tokenManager: TokenManager): AuthRepository
{
        override suspend fun login(email: String, password: String): Flow<Resource<AuthResponse>> = flow {
            emit(Resource.Loading()) // Phát ra tín hiệu load xoay vòng vòng khi người dùng ấn đăng nhập
            try {
                val response = apiService.login(AuthRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.success && apiResponse.data != null) {
                            // Lưu thông tin đăng nhập
                            tokenManager.saveUserInfo(
                                apiResponse.data.accessToken,
                                apiResponse.data.refreshToken,
                                apiResponse.data.email,
                                apiResponse.data.fullName,
                                apiResponse.data.imageUrl,
                                apiResponse.data.userId
                            )
                            emit(Resource.Success(apiResponse.data))
                        } else {
                            emit(Resource.Error(apiResponse.message))
                        }
                    }
            } else {
                emit(Resource.Error("Đăng nhập thất bại: ${response.message()}"))
            }
        }
        catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        }
        catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.register(registerRequest)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                }
            } else {
                emit(Resource.Error("Đăng ký thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun forgotPassword(email: String, newPassword: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email, newPassword))
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                }
            } else {
                emit(Resource.Error("Đặt lại mật khẩu thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun sendOtp(email: String, forRegistration: Boolean): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.sendOtp(OtpRequest(email, forRegistration))
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                }
            } else {
                emit(Resource.Error("Gửi OTP thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun verifyOtp(otp: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.verifyOtp(OtpVerifyRequest(otp))
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                }
            } else {
                emit(Resource.Error("Xác thực OTP thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun refreshToken(refreshToken: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.refreshToken(refreshToken)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        // Cập nhật access token mới
                        tokenManager.updateAccessToken(apiResponse.data)
                        emit(Resource.Success(apiResponse.data))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                }
            } else {
                emit(Resource.Error("Làm mới token thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun logout() {
        tokenManager.clearUserInfo()
    }
}