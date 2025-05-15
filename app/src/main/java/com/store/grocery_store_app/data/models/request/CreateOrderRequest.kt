package com.store.grocery_store_app.data.models.request
data class CreateOrderRequest(
    val addressId: Long?,
    val voucherId: Long?,
    val paymentMethod: String,
    val orderItems: List<OrderItemRequest>
)
