package com.store.grocery_store_app.data.models.response

import java.math.BigDecimal

data class OrderItemResponse(
    val id: Long,
    val quantity: Int,
    val price: BigDecimal,
    val product: ProductResponse,
    val reviewed : Boolean,
    val canReview : Boolean
)