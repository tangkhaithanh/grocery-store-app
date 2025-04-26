package com.store.grocery_store_app.data.models.response

data class ApiResponse<T> (
    val success: Boolean,
    val message: String,
    val data: T?,
    val timestamp: String
)
