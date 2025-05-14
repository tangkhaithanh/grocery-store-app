package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.response.ApiResponse
import com.store.grocery_store_app.data.models.response.FlashSaleResponse
import com.store.grocery_store_app.data.models.response.OrderResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FlashSaleRepository {
    suspend fun getFlashSale(): Flow<Resource<List<FlashSaleResponse>>>

}