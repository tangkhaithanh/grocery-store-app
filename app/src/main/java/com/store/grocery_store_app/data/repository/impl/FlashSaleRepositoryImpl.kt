package com.store.grocery_store_app.data.repository.impl

import android.util.Log
import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.response.ApiResponse
import com.store.grocery_store_app.data.models.response.FlashSaleResponse
import com.store.grocery_store_app.data.repository.FlashSaleRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FlashSaleRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : FlashSaleRepository {
    override suspend fun getFlashSale(): Flow<Resource<List<FlashSaleResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFlashSale()

            if(response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val flashSales = apiResponse.data
                        emit(Resource.Success(flashSales))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy sản phẩm FL thất bại: ${response.message()}"))
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

    override suspend fun getFlashSaleByFLI(id: Long): Flow<Resource<FlashSaleResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFlashSaleByFLI(id)

            if(response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val flashSale = apiResponse.data
                        emit(Resource.Success(flashSale))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy sản phẩm FL thất bại: ${response.message()}"))
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