package com.store.grocery_store_app.data.models.response

import com.store.grocery_store_app.data.models.OrderItem
import java.math.BigDecimal

data class OrderItemResponse(
    val id: Long,
    val quantity: Int,
    val price: BigDecimal,
    val product: ProductResponse,
    val reviewed : Boolean,
    val canReview : Boolean
)


fun OrderItemResponse.toOrderItem(orderId : Long) : OrderItem {
    return OrderItem(
        orderId = orderId.toString(),
        orderItemId = this.id.toString(),
        productId = this.product.id,
        storeName = "Grocery App",
        productName = this.product.name,
        productDescription = this.product.description,
        quantity = this.quantity,
        sellPrice = null,
        buyPrice = null,
        totalAmount = 0,
        imageRes = "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75",
        canReview = this.canReview,
        reviewed = this.reviewed
    )
}