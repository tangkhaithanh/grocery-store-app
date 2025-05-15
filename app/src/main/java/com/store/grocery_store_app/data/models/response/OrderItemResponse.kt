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
        imageRes = this.product.imageUrls[0],
        canReview = this.canReview,
        reviewed = this.reviewed
    )
}