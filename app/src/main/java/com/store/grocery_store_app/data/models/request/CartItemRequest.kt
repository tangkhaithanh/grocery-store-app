package com.store.grocery_store_app.data.models.request

import com.store.grocery_store_app.data.models.response.ProductResponse
import java.math.BigDecimal


data class CartItemRequest(
    val id: Long?,
    val flashSaleId: Long?,
    val quantity:Int,
    val price: BigDecimal,
    val product: ProductSimpleRequest
)