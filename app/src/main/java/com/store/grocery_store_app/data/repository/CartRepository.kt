package com.store.grocery_store_app.data.repository

import android.graphics.pdf.PdfDocument.Page
import com.store.grocery_store_app.data.models.request.CartItemRequest
import com.store.grocery_store_app.data.models.response.CartResponse
import com.store.grocery_store_app.data.models.response.PagedResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun insertProductIntoCart(cartItemRequest: CartItemRequest): Flow<Resource<Any>>

    suspend fun getAllCartItem(): Flow<Resource<CartResponse>>
}