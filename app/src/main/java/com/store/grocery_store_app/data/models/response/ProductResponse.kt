package com.store.grocery_store_app.data.models.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductResponse (
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val quantity: Int,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val categoryName: String,
    val categoryImage: String,
    val imageUrls: List<String>,
    val averageRating: Double?,
    val soldCount: Int,
    val effectivePrice: BigDecimal
)