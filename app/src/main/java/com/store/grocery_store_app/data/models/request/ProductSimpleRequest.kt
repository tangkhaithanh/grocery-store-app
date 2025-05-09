package com.store.grocery_store_app.data.models.request

import java.math.BigDecimal

data class ProductSimpleRequest(
    val id:Long,
    val name: String,
    val price: BigDecimal,
    val imageUrl: String?
)