package com.store.grocery_store_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.data.repository.ProductRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewArrivalsState(
    val isLoading: Boolean = false,
    val newArrivals: List<ProductResponse> = emptyList(),
    val error: String? = null
)
@HiltViewModel
class NewArrivalsViewModel @Inject constructor(private val productRepository: ProductRepository):ViewModel() {

    private val _state = MutableStateFlow(NewArrivalsState())
    val state: StateFlow<NewArrivalsState> = _state.asStateFlow()

    init {
        loadNewArrivals()
    }

    fun loadNewArrivals(refresh: Boolean = false) {
        viewModelScope.launch {
            // Lấy 10 sản phẩm mới nhất
            val currentPage = 0
            val pageSize = 10

            if (refresh) {
                _state.update { it.copy(
                    newArrivals = emptyList(),
                    error = null
                ) }
            }

            _state.update { it.copy(isLoading = true, error = null) }

            // Sử dụng API getAllProducts có sẵn, vì nó đã sắp xếp theo ngày tạo giảm dần
            productRepository.getAllProducts(page = currentPage, size = pageSize).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Đã xử lý ở trên
                    }
                    is Resource.Success -> {
                        val products = result.data ?: emptyList()

                        _state.update {
                            it.copy(
                                isLoading = false,
                                newArrivals = products,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun refreshProducts() {
        loadNewArrivals(refresh = true)
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}