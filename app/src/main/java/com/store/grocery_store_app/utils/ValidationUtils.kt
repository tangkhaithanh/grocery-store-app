package com.store.grocery_store_app.utils

object ValidationUtils {
    /**
     * Kiểm tra địa chỉ email có hợp lệ hay không
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Kiểm tra số điện thoại Việt Nam có hợp lệ hay không
     * Hỗ trợ định dạng: 0912345678, +84912345678
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = Regex("^(0|\\+84)(3|5|7|8|9)[0-9]{8}$")
        return phoneRegex.matches(phone)
    }

    /**
     * Kiểm tra mật khẩu có hợp lệ hay không
     * Yêu cầu: Ít nhất 6 ký tự
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Kiểm tra xem hai mật khẩu có khớp nhau không
     */
    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    /**
     * Kiểm tra tên có hợp lệ hay không
     * Yêu cầu: Không được trống và chỉ chứa chữ cái, khoảng trắng, và một số ký tự đặc biệt được sử dụng trong tên
     */
    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false
        // Regex này khớp với chữ cái từ bất kỳ ngôn ngữ nào, khoảng trắng, dấu chấm, dấu nháy đơn và dấu gạch ngang
        val nameRegex = Regex("^[\\p{L} .'-]+$")
        return nameRegex.matches(name)
    }

    /**
     * Kiểm tra mã OTP có hợp lệ hay không
     * Yêu cầu: 6 chữ số
     */
    fun isValidOtp(otp: String): Boolean {
        val otpRegex = Regex("^[0-9]{6}$")
        return otpRegex.matches(otp)
    }

    /**
     * Kiểm tra một chuỗi có phải là chuỗi rỗng hay không
     */
    fun isEmptyOrBlank(text: String): Boolean {
        return text.isBlank()
    }

    /**
     * Kiểm tra độ dài tối thiểu của chuỗi
     */
    fun hasMinLength(text: String, minLength: Int): Boolean {
        return text.length >= minLength
    }

    /**
     * Kiểm tra có phải chỉ chứa chữ số hay không
     */
    fun isNumeric(text: String): Boolean {
        return text.all { it.isDigit() }
    }

    /**
     * Kiểm tra có phải chỉ chứa chữ cái hay không
     */
    fun isAlphabetic(text: String): Boolean {
        return text.all { it.isLetter() || it.isWhitespace() }
    }
}