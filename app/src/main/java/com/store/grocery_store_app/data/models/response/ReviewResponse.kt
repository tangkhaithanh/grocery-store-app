package com.store.grocery_store_app.data.models.response

data class ReviewResponse (
    val id: Long,
    val rating: Int,
    val comment: String?,
    val imageUrls: List<String>,
    val userFullName: String,
    val userId: Long,
    val orderItemId: Long,
    val createdAt: String
)
