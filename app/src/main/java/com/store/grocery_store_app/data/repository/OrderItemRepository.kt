package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.response.OrderItemResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface OrderItemRepository {
    suspend fun getOrderItem(orderItemId : Long): Flow<Resource<OrderItemResponse>>
}