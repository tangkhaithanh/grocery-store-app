package com.store.grocery_store_app.data.repository.impl

import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.data.repository.ProductRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val apiService: ApiService):ProductRepository {
    override suspend fun getBestSellerProducts(page: Int, size: Int): Flow<Resource<List<ProductResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getBestSellerProducts(page, size)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val products = apiResponse.data.content
                        emit(Resource.Success(products))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy sản phẩm bán chạy thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun getAllProducts(page: Int, size: Int): Flow<Resource<List<ProductResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAllProducts(page, size)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val products = apiResponse.data.content
                        emit(Resource.Success(products))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy danh sách sản phẩm thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun getProductById(id: Long): Flow<Resource<ProductResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getProductDetail(id)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        emit(Resource.Success(apiResponse.data))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy chi tiết sản phẩm thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun getProductsByCategory(
        categoryId: Long,
        page: Int,
        size: Int
    ): Flow<Resource<List<ProductResponse>>> = flow{
        emit(Resource.Loading())
        try {
            val response = apiService.getProductsByCategory(categoryId,page,size)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val products = apiResponse.data.content
                        emit(Resource.Success(products))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy sản phẩm theo danh mục thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun searchProducts(
        query: String,
        page: Int,
        size: Int
    ): Flow<Resource<List<ProductResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.searchProducts(keyword = query, page = page, size = size)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val products = apiResponse.data.content
                        emit(Resource.Success(products))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Tìm kiếm sản phẩm thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }
}