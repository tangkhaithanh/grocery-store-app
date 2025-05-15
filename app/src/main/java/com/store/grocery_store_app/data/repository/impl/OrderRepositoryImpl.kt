package com.store.grocery_store_app.data.repository.impl

import android.util.Log
import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.StatusOrderType
import com.store.grocery_store_app.data.models.request.CreateOrderRequest
import com.store.grocery_store_app.data.models.response.CreateOrderResponse
import com.store.grocery_store_app.data.models.response.OrderResponse
import com.store.grocery_store_app.data.repository.OrderRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): OrderRepository{
    override suspend fun getOrders(page: Int, size: Int, typeStatusOrder: StatusOrderType): Flow<Resource<List<OrderResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getOrders(page, size, typeStatusOrder)

            if(response.isSuccessful) {
                response.body()?.let { apiResponse ->
                if (apiResponse.success && apiResponse.data != null) {
                    val orders = apiResponse.data.content
                    emit(Resource.Success(orders))
                } else {
                    emit(Resource.Error(apiResponse.message))
                }
            } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy đơn hàng thất bại: ${response.message()}"))
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

    override suspend fun getOrder(orderId: Long): Flow<Resource<OrderResponse>> = flow{
        emit(Resource.Loading())
        try {
            val response = apiService.getOrder(orderId)

            if(response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val order = apiResponse.data
                        emit(Resource.Success(order))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy đơn hàng thất bại: ${response.message()}"))
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

    override suspend fun createOrder(request: CreateOrderRequest): Flow<Resource<CreateOrderResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createOrder(request)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        emit(Resource.Success(apiResponse.data))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Đặt hàng thất bại: ${response.message()}"))
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