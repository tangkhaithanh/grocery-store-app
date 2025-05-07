package com.store.grocery_store_app.data.models.response;

import com.store.grocery_store_app.data.models.StatusOrderType
import java.math.BigDecimal

data class OrderResponse(
    val id : Long,
    val totalAmount : BigDecimal,
    val deliveredAt : String,
    val status : StatusOrderType,
    val orderItems : List<OrderItemResponse>
)
