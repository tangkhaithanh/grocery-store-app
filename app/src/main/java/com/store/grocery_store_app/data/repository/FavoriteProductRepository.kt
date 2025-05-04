package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FavoriteProductRepository {
    suspend fun getFavouriteProducts(): Flow<Resource<List<ProductResponse>>>
    suspend fun addToFavourite(productId: Long): Flow<Resource<Boolean>>
    suspend fun removeFromFavourite(productId: Long): Flow<Resource<Boolean>>
    fun isFavourite(productId: Long): Flow<Boolean>
}