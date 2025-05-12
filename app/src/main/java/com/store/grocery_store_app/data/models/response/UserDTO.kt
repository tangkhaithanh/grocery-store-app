package com.store.grocery_store_app.data.models.response

data class UserDTO(
    val id: Long,
    val fullName: String,
    val email: String,
    val phone: String,
    val gender: String,
    val imageUrl: String
)