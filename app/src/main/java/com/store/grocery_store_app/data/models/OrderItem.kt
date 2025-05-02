package com.store.grocery_store_app.data.models
import java.math.BigDecimal
import com.store.grocery_store_app.data.models.StatusOrderType

data class OrderItem(
    val orderId: String,
    val storeName: String,
    val productName: String,
    val productDescription: String,
    val imageRes: Int,
    val quantity: Int,
    val sellPrice: BigDecimal?,
    val buyPrice: BigDecimal,
    val totalAmount: Int,
    val canReview : Boolean
)