package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.data.models.response.VoucherResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface VoucherRepository {
    suspend fun getAllVoucher(): Flow<Resource<List<VoucherResponse>>>
}