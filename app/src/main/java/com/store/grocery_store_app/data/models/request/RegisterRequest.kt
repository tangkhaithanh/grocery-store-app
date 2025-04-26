package com.store.grocery_store_app.data.models.request

data class RegisterRequest (
    val fullName: String,
    val phone: String,
    val gender: String,
    val email: String,
    val password: String
)