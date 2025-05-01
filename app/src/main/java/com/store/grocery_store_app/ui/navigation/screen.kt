package com.store.grocery_store_app.ui.navigation
import com.store.grocery_store_app.utils.AuthPurpose
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Login : Screen("login")

    object EmailVerification : Screen("email_verification/{purpose}") {
        fun createRoute(purpose: AuthPurpose) = "email_verification/${purpose.name}"
    }

    object OtpVerification : Screen("otp_verification/{purpose}/{email}") {
        fun createRoute(purpose: AuthPurpose, email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "otp_verification/${purpose.name}/$encodedEmail"
        }
    }

    object Register : Screen("register/{email}") {
        fun createRoute(email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "register/$encodedEmail"
        }
    }

    object ResetPassword : Screen("reset_password/{email}") {
        fun createRoute(email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "reset_password/$encodedEmail"
        }
    }

    object Home : Screen("home")

    object Order : Screen("order")
}