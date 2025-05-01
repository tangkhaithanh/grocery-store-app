package com.store.grocery_store_app.data.models.response;

import com.store.grocery_store_app.data.models.StatusOrderType
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val id : Long,
    val totalAmount : BigDecimal,
    val deliveryAt : String,
    val status : StatusOrderType,
    val orderItems : List<OrderItemResponse>
)
