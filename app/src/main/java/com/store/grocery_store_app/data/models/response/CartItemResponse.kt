package com.store.grocery_store_app.data.models.response

import com.store.grocery_store_app.data.models.request.ProductSimpleRequest
import java.math.BigDecimal

data class CartItemResponse(
    val id: Long?,
    val flashSaleId: Long?,
    val quantity:Int,
    val price: BigDecimal,
    val product: ProductResponse
)
