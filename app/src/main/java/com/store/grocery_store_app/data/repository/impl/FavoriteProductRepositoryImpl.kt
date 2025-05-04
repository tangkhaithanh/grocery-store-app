package com.store.grocery_store_app.data.repository.impl

import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.data.repository.FavoriteProductRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FavoriteProductRepositoryImpl @Inject constructor(private val apiService: ApiService):FavoriteProductRepository {

    private val _favouriteProductIds = MutableStateFlow<Set<Long>>(emptySet())
    val favouriteProductIds = _favouriteProductIds.asStateFlow()

    override suspend fun getFavouriteProducts(): Flow<Resource<List<ProductResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFavouriteProducts()
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val products = apiResponse.data
                        // Cập nhật danh sách ID sản phẩm yêu thích
                        _favouriteProductIds.value = products.map { it.id }.toSet()
                        emit(Resource.Success(products))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy sản phẩm yêu thích thất bại: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi: ${e.message}"))
        }
    }

    override suspend fun addToFavourite(productId: Long): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.addToFavourite(productId)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        // Cập nhật danh sách sản phẩm yêu thích
                        _favouriteProductIds.update { it + productId }
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Thêm sản phẩm yêu thích thất bại: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi: ${e.message}"))
        }
    }

    override suspend fun removeFromFavourite(productId: Long): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.removeFromFavourite(productId)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        // Cập nhật danh sách sản phẩm yêu thích
                        _favouriteProductIds.update { it - productId }
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Xóa sản phẩm yêu thích thất bại: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi: ${e.message}"))
        }
    }

    override fun isFavourite(productId: Long): Flow<Boolean> {
        return favouriteProductIds.map { it.contains(productId) }
    }
}