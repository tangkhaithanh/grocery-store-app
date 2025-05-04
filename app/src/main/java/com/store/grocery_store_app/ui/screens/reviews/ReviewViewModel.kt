package com.store.grocery_store_app.ui.screens.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.data.models.response.ReviewStatsResponse
import com.store.grocery_store_app.data.repository.ReviewRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val reviewRepository: ReviewRepository
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
                                error = result.message ?: "Không thể tải đánh giá"
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
}