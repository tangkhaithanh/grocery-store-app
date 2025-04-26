package com.store.grocery_store_app.data.models.request

data class ForgotPasswordRequest (
    val email: String,
    val newPassword: String
)
