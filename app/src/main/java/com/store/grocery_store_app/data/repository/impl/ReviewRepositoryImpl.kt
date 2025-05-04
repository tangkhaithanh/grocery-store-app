package com.store.grocery_store_app.data.repository.impl

import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.data.models.response.ReviewStatsResponse
import com.store.grocery_store_app.data.repository.ReviewRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(private val apiService: ApiService):ReviewRepository
{
    override suspend fun getProductReviews(
        productId: Long,
        page: Int,
        size: Int
    ): Flow<Resource<List<ReviewResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getProductReviews(productId, page, size)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val reviews = apiResponse.data.content
                        emit(Resource.Success(reviews))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy đánh giá thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun getProductReviewStats(productId: Long): Flow<Resource<ReviewStatsResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getProductReviewStats(productId)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        emit(Resource.Success(apiResponse.data))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy thống kê đánh giá thất bại: ${response.message()}"))
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