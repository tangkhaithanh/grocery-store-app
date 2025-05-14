package com.store.grocery_store_app.data.models.response

import java.math.BigDecimal

data class FlashSaleItemResponse(
    val id : Long,
    val flashSalePrice: BigDecimal,
    val stockQuantity: Int,
    val soldQuantity:Int,
    val maxPerCustomer:Int,
    val product:ProductResponse
)