package com.store.grocery_store_app.ui.screens.reviews

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.api.CloudinaryService
import com.store.grocery_store_app.data.models.response.OrderItemResponse
import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.data.models.response.ReviewStatsResponse
import com.store.grocery_store_app.data.repository.CloudinaryRepository
import com.store.grocery_store_app.data.repository.OrderItemRepository
import com.store.grocery_store_app.data.repository.ReviewRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named

data class ReviewState(
    val isLoading: Boolean = false,
    val reviews: List<ReviewResponse> = emptyList(),
    val reviewStats: ReviewStatsResponse? = null,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val error: String? = null
)
@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val orderItemRepository: OrderItemRepository,
    private val cloudinaryRepository: CloudinaryRepository,
) : ViewModel()
{
    private val _state = MutableStateFlow(ReviewState())
    val state: StateFlow<ReviewState> = _state.asStateFlow()


    /**
     * Tải danh sách đánh giá cho một sản phẩm
     */
    fun loadProductReviews(productId: Long, refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _state.update {
                    it.copy(
                        isLoading = true,
                        error = null,
                        reviews = emptyList(),
                        currentPage = 0
                    )
                }
            } else {
                _state.update { it.copy(isLoading = true, error = null) }
            }

            reviewRepository.getProductReviews(
                productId = productId,
                page = if (refresh) 0 else _state.value.currentPage,
                size = 10
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val reviewsList = result.data ?: emptyList()
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = null,
                                reviews = if (refresh || currentState.currentPage == 0) {
                                    reviewsList
                                } else {
                                    currentState.reviews + reviewsList
                                },
                                currentPage = currentState.currentPage + 1,
                                hasMorePages = reviewsList.isNotEmpty() && reviewsList.size == 10
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Không thể lấy đơn hàng sản phẩm"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Đã xử lý ở trên
                    }
                }
            }
        }
    }

    /**
     * Tải thống kê đánh giá cho một sản phẩm
     */
    fun loadReviewStats(productId: Long) {
        viewModelScope.launch {
            reviewRepository.getProductReviewStats(productId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(reviewStats = result.data)
                        }
                    }
                    is Resource.Error -> {
                        // Chỉ ghi log lỗi nhưng không cập nhật state vì đây là thông tin phụ
                        println("Error loading review stats: ${result.message}")
                    }
                    is Resource.Loading -> {
                        // Không cần hiển thị trạng thái loading cho thống kê
                    }
                }
            }
        }
    }

    /**
     * Xóa thông báo lỗi trong state
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private val _orderItem = MutableStateFlow<OrderItemResponse?>(null)
    val orderItem: StateFlow<OrderItemResponse?> = _orderItem

    fun loadOrderItemById(orderItemId: Long) {
        viewModelScope.launch {
            orderItemRepository.getOrderItem(orderItemId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _orderItem.value = result.data
                        _state.update { it.copy(isLoading = false, error = null) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }



    private val _uploadedImageUrl = MutableStateFlow<String?>(null)
    val uploadedImageUrl: StateFlow<String?> = _uploadedImageUrl.asStateFlow()

    fun uploadImageToCloudinary(uri: Uri) {
        viewModelScope.launch {
            cloudinaryRepository.uploadImage(uri).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _uploadedImageUrl.value = result.data
                        _state.update { it.copy(isLoading = false, error = null) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }



}