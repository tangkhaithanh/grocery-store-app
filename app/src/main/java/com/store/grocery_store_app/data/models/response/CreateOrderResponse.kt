package com.store.grocery_store_app.data.models.response

import com.store.grocery_store_app.data.models.StatusOrderType
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderResponse(
    val orderId: Long,
    val totalAmount: BigDecimal,
    val status: StatusOrderType,
    val paymentMethod: String,
    val createdAt: String,
    val message: String,

    // Thông tin địa chỉ giao hàng (snapshot)
    val shippingUserName: String? = null,
    val shippingPhoneNumber: String? = null,
    val shippingCity: String? = null,
    val shippingDistrict: String? = null,
    val shippingStreetAddress: String? = null
)