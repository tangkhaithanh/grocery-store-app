package com.store.grocery_store_app.ui.screens.ProductDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.data.repository.FavoriteProductRepository
import com.store.grocery_store_app.data.repository.ProductRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
data class SimilarProductsState(
    val isLoading: Boolean = false,
    val products: List<ProductResponse> = emptyList(),
    val error: String? = null
)
data class  ProductDetailsState(
    val isLoading: Boolean = true,
    val product: ProductResponse? = null,
    val error: String? = null,
    val currentImageIndex: Int = 0,
    val isFavorite: Boolean = false
)
@HiltViewModel
class ProductDetailsViewModel @Inject constructor(private val productRepository: ProductRepository,
                                                  private val favoriteRepository: FavoriteProductRepository
): ViewModel()
{
    private val _state = MutableStateFlow(ProductDetailsState())
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _similarProductsState = MutableStateFlow(SimilarProductsState())
    val similarProductsState: StateFlow<SimilarProductsState> = _similarProductsState.asStateFlow()

    fun loadProductDetails(productId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                productRepository.getProductById(productId).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            // Already handled above
                        }
                        is Resource.Success -> {
                            _state.update { it.copy(
                                isLoading = false,
                                product = result.data,
                                error = null
                            ) }
                            // Check if product is in favorites
                            checkFavoriteStatus(productId)
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(
                                isLoading = false,
                                error = result.message
                            ) }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Lỗi không xác định: ${e.message}"
                ) }
            }
        }
    }

    private fun checkFavoriteStatus(productId: Long) {
        viewModelScope.launch {
            favoriteRepository.isFavourite(productId).collectLatest { isFavorite ->
                _state.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    fun changeCurrentImage(index: Int) {
        if (index >= 0 && index < (_state.value.product?.imageUrls?.size ?: 0)) {
            _state.update { it.copy(currentImageIndex = index) }
        }
    }

    fun toggleFavorite() {
        val productId = _state.value.product?.id ?: return
        val currentFavoriteStatus = _state.value.isFavorite

        viewModelScope.launch {
            try {
                if (currentFavoriteStatus) {
                    favoriteRepository.removeFromFavourite(productId).collect { result ->
                        if (result is Resource.Success) {
                            _state.update { it.copy(isFavorite = false) }
                        }
                    }
                } else {
                    favoriteRepository.addToFavourite(productId).collect { result ->
                        if (result is Resource.Success) {
                            _state.update { it.copy(isFavorite = true) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    // Add this method to load similar products
    fun loadSimilarProducts() {
        state.value.product?.let { product ->
            viewModelScope.launch {
                _similarProductsState.update { it.copy(isLoading = true, error = null) }

                // Get products in the same category but filter out the current product later in UI
                productRepository.getProductsByCategory(
                    categoryId = product.categoryId,
                    page = 0,
                    size = 6 // Limit to 6 products for grid view (3 rows of 2)
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _similarProductsState.update {
                                it.copy(
                                    products = result.data ?: emptyList(),
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Error -> {
                            _similarProductsState.update {
                                it.copy(
                                    error = result.message,
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Loading -> {
                            // Already handled above
                        }
                    }
                }
            }
        }
    }
}