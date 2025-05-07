package com.store.grocery_store_app.data.repository.impl

import android.util.Log
import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.response.OrderItemResponse
import com.store.grocery_store_app.data.repository.OrderItemRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OrderItemRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OrderItemRepository {
    override suspend fun getOrderItem(orderItemId: Long): Flow<Resource<OrderItemResponse>> = flow{
        emit(Resource.Loading())
        try {
            val response = apiService.getOrderItemById(orderItemId)

            if(response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val orders = apiResponse.data
                        emit(Resource.Success(orders))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy đơn hàng sản phẩm thất bại: ${response.message()}"))
            }
        }
        catch (e: HttpException) {
            Log.d("Error", e.message())
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }
}