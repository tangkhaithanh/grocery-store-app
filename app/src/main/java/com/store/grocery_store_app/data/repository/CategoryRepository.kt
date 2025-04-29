package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.response.CategoryResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(page: Int = 0, size: Int = 100): Flow<Resource<List<CategoryResponse>>>
}