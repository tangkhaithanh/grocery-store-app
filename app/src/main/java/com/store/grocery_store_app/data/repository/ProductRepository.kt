package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getBestSellerProducts(page: Int = 0, size: Int = 20): Flow<Resource<List<ProductResponse>>>

    // Thêm phương thức mới để lấy tất cả sản phẩm
    suspend fun getAllProducts(page: Int = 0, size: Int = 20): Flow<Resource<List<ProductResponse>>>

    suspend fun getProductById(id: Long): Flow<Resource<ProductResponse>>

    suspend fun getProductsByCategory(
        categoryId: Long,
        page: Int = 0,
        size: Int = 20
    ): Flow<Resource<List<ProductResponse>>>

}