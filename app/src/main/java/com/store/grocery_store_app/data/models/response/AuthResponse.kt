package com.store.grocery_store_app.data.models.response

data class AuthResponse (
    val accessToken: String,
    val refreshToken: String,
    val fullName: String,
    val email: String,
    val imageUrl: String?, // có thể null được
    val userId: Long
)
