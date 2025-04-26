package com.store.grocery_store_app.ui.screens.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.repository.AuthRepository
import com.store.grocery_store_app.utils.AuthPurpose
import com.store.grocery_store_app.utils.Resource
import com.store.grocery_store_app.utils.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtpVerificationState(
    val email: String = "",
    val purpose: AuthPurpose = AuthPurpose.REGISTRATION,
    val otp: String = "",
    val otpError: String = "",
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val error: String? = null,
    val resendCooldown: Int = 0
)
@HiltViewModel
class OtpVerificationViewModel  @Inject constructor(private val authRepository: AuthRepository): ViewModel(){
    private val _state = MutableStateFlow(OtpVerificationState())
    val state: StateFlow<OtpVerificationState> = _state.asStateFlow()
    private var cooldownJob: Job? = null
    // Khi màn hình được mở, init được gọi để bắt đầu countdown:
    fun init(email: String, purpose: AuthPurpose) {
        _state.update {
            it.copy(
                email = email,
                purpose = purpose
            )
        }
        startResendCooldown()
    }
    fun onOtpChange(otp: String) {
        _state.update { it.copy(otp = otp, otpError = "") }

        // Auto verify when OTP length is 6
        if (otp.length == 6) {
            verifyOtp()
        }
    }

    private fun startResendCooldown(seconds: Int = 60) {
        cooldownJob?.cancel() // Nếu đang đếm ngược, hủy nó ngay lập tức
        _state.update { it.copy(resendCooldown = seconds) } // cập nhật lại số giây để countdown (ở đây là 60s)

        cooldownJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _state.update { it.copy(resendCooldown = i) } // cập nhật lại số giây
                delay(1000)
            }
            _state.update { it.copy(resendCooldown = 0) } // kết thúc countdown cập nhật lại thành 0 để kết thúc
        }
    }

    fun verifyOtp() {
        val otp = state.value.otp

        // Validate OTP
        if (otp.isBlank()) {
            _state.update { it.copy(otpError = "Vui lòng nhập mã OTP") }
            return
        } else if (!ValidationUtils.isValidOtp(otp)) {
            _state.update { it.copy(otpError = "Mã OTP phải có 6 chữ số") }
            return
        }

        viewModelScope.launch {
            authRepository.verifyOtp(otp).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isVerified = true,
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

    fun resendOtp() {
        val email = state.value.email
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
                                otp = "", // xóa mã otp cũ trên textbox
                                error = null
                            )
                        }
                        startResendCooldown() // bắt đầu đếm ngược lại
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
    //Thường được gọi từ UI khi người dùng đã thấy thông báo lỗi và mình muốn ẩn lỗi khỏi giao diện (ví dụ: đóng Snackbar hoặc Dialog thông báo lỗi).

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    //Được gọi khi ViewModel bị huỷ (ví dụ: khi người dùng rời màn hình).

    override fun onCleared() {
        super.onCleared()
        cooldownJob?.cancel()
    }
}