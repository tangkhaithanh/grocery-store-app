package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.data.models.response.ReviewStatsResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    suspend fun getProductReviews(
        productId: Long,
        page: Int = 0,
        size: Int = 10
    ): Flow<Resource<List<ReviewResponse>>>

    suspend fun getProductReviewStats(
        productId: Long
    ): Flow<Resource<ReviewStatsResponse>>
}