package com.store.grocery_store_app.data.models.response

import com.store.grocery_store_app.data.models.request.CartItemRequest

data class CartResponse(
    val id : Long,
    val cartItems: List<CartItemRequest>
)