package com.store.grocery_store_app.data.models.response

data class CategoryResponse (
    val id: Long,
    val name: String,
    val imageUrl: String,
    val createdAt: String? = null, // có thể null
    val updatedAt: String? = null  // có thể null
)