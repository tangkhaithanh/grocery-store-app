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

data class ProductsState(
    val isLoading: Boolean = false,
    val bestSellerProducts: List<ProductResponse> = emptyList(),
    val error: String? = null,
    val page: Int = 0,
    val hasMoreItems: Boolean = true
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    init {
        loadBestSellerProducts()
    }

    fun loadBestSellerProducts(refresh: Boolean = false) {
        viewModelScope.launch {
            // Limit to the first page with size 10 for horizontal scroll component
            val currentPage = 0
            val pageSize = 10

            if (refresh) {
                _state.update { it.copy(
                    bestSellerProducts = emptyList(),
                    error = null
                ) }
            }

            _state.update { it.copy(isLoading = true, error = null) }

            productRepository.getBestSellerProducts(page = currentPage, size = pageSize).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Already handled above
                    }
                    is Resource.Success -> {
                        val products = result.data ?: emptyList()

                        _state.update {
                            it.copy(
                                isLoading = false,
                                bestSellerProducts = products,
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
        loadBestSellerProducts(refresh = true)
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}