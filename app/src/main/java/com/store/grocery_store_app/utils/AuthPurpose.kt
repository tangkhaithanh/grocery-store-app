package com.store.grocery_store_app.utils

/**
 * Enum class xác định mục đích của luồng xác thực.
 * REGISTRATION: Dùng cho quá trình đăng ký tài khoản.
 * PASSWORD_RESET: Dùng cho quá trình đặt lại mật khẩu.
 */
enum class AuthPurpose {
    /**
     * Mục đích xác thực là để đăng ký tài khoản mới.
     * Sẽ quy định việc gửi OTP để xác minh email cho người dùng mới.
     */
    REGISTRATION,

    /**
     * Mục đích xác thực là để đặt lại mật khẩu.
     * Sẽ quy định việc gửi OTP để xác minh email cho tài khoản đã tồn tại.
     */
    PASSWORD_RESET
}