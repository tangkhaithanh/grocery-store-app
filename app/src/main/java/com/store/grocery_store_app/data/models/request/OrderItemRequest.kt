package com.store.grocery_store_app.data.models.request

import java.math.BigDecimal

data class OrderItemRequest(
    val productId: Long,
    val flashSaleItemId: Long?,
    val quantity: Int,
    val price: BigDecimal
)