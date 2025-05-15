package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.StatusOrderType
import com.store.grocery_store_app.data.models.response.CategoryResponse
import com.store.grocery_store_app.data.models.response.OrderResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrders(page: Int = 0, size: Int = 20, typeStatusOrder: StatusOrderType): Flow<Resource<List<OrderResponse>>>
    suspend fun getOrder(orderId: Long): Flow<Resource<OrderResponse>>
    suspend fun cancelOrder(orderId: Long) : Flow<Resource<Boolean>>
}