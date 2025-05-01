package com.store.grocery_store_app.data.models
import java.math.BigDecimal

data class OrderGroup(
    val orderId: String,
    val storeName: String,
    val items: List<OrderItem>,
    val totalAmount: Int
)