package com.store.grocery_store_app.data.repository.impl

import android.util.Log
import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.request.CartItemRequest
import com.store.grocery_store_app.data.models.response.CartResponse
import com.store.grocery_store_app.data.repository.CartRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CartRepository {
    override suspend fun insertProductIntoCart(cartItemRequest: CartItemRequest): Flow<Resource<Any>> = flow{
        emit(Resource.Loading())
        try {
            val response = apiService.insertProductIntoCart(cartItemRequest)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Thêm sản phẩm vô giỏ hàng thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }

    }

    override suspend fun getAllCartItem(): Flow<Resource<CartResponse>> = flow {
        emit(Resource.Loading())
        val response = apiService.getAllCartItem()                         // có thể ném HttpException
        if (response.isSuccessful) {
            val apiResp = response.body() ?: throw IllegalStateException("Server trả về rỗng")
            if (apiResp.success && apiResp.data != null) {
                emit(Resource.Success(apiResp.data))
            } else {
                throw IllegalStateException(apiResp.message ?: "Lỗi dữ liệu")
            }
        } else {
            throw HttpException(response)
        }
    }
        .catch { e ->
            // Tất cả exception đều vào đây
            val msg = when (e) {
                is IOException    -> "Không thể kết nối đến máy chủ"
                is HttpException  -> "Lỗi HTTP: ${e.code()} ${e.message()}"
                else              -> e.message ?: "Lỗi không xác định"
            }
            emit(Resource.Error(msg))
        }

    override suspend fun removeCartItem(id: Long): Flow<Resource<Any>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.removeCartItem(id)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Xóa sản phẩm vô giỏ hàng thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun updateProductIntoCart(cartItemRequest: CartItemRequest): Flow<Resource<Any>> = flow{
        emit(Resource.Loading())
        try {
            val response = apiService.updateProductIntoCart(cartItemRequest)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Cập nhật số lượng sản phẩm vô giỏ hàng thất bại: ${response.message()}"))
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