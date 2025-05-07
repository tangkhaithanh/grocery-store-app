package com.store.grocery_store_app.data.models
import java.math.BigDecimal
import com.store.grocery_store_app.data.models.StatusOrderType

data class OrderItem(
    val orderId: String,
    val orderItemId: String,
    val storeName: String,
    val productId : Long,
    val productName: String,
    val productDescription: String,
    val imageRes: String,
    val quantity: Int,
    val sellPrice: BigDecimal?,
    val buyPrice: BigDecimal?,
    val totalAmount: Int,
    val reviewed: Boolean,
    val canReview : Boolean
)