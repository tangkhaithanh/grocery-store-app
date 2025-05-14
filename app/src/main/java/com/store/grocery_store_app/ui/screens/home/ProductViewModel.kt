package com.store.grocery_store_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.response.FlashSaleItemResponse
import com.store.grocery_store_app.data.models.response.FlashSaleResponse
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.data.repository.CategoryRepository
import com.store.grocery_store_app.data.repository.FlashSaleRepository
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
    val hasMoreItems: Boolean = true,

    // Trạng thái của sản phẩm theo danh mục:
    val categoryId: Long = 0,
    val categoryName: String = "",
    val productsByCategory: List<ProductResponse> = emptyList(),
    val categoryProductsLoading: Boolean = false,
    val categoryProductsError: String? = null,
    val categoryProductsPage: Int = 0,
    val hasMoreCategoryProducts: Boolean = true,
    val flashSales : List<FlashSaleResponse> = emptyList(),
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val flashSaleRepository: FlashSaleRepository
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

    // Thiết lập danh mục hiện tại và tải tên danh mục:
    fun setCategoryAndLoadName(categoryId: Long) {
        _state.update { it.copy(
            categoryId = categoryId,
            productsByCategory = emptyList(),
            categoryProductsPage = 0,
            hasMoreCategoryProducts = true
        ) }
        loadCategoryName(categoryId)
        loadProductsByCategory(categoryId, 0,20,true)
    }

    // Hàm tải tên danh mục
    private fun loadCategoryName(categoryId: Long) {
        viewModelScope.launch {
            categoryRepository.getCategories().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Tìm danh mục có id phù hợp
                        val category = result.data?.find { it.id == categoryId }
                        category?.let {
                            _state.update { state -> state.copy(categoryName = it.name) }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    // Hàm tải sản phẩm theo danh mục
    fun loadProductsByCategory(categoryId: Long, page: Int = 0, size: Int = 20, refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _state.update {
                    it.copy(
                        productsByCategory = emptyList(),
                        categoryProductsError = null,
                        categoryProductsPage = 0,
                        hasMoreCategoryProducts = true
                    )
                }
            }

            _state.update { it.copy(categoryProductsLoading = true, categoryProductsError = null) }

            productRepository.getProductsByCategory(categoryId, page, size).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val newProducts = result.data ?: emptyList()
                        // Nếu không có sản phẩm mới hoặc số lượng nhỏ hơn pageSize, coi như đã hết sản phẩm
                        val hasMore = newProducts.isNotEmpty() && newProducts.size >= size

                        _state.update { state ->
                            state.copy(
                                productsByCategory = if (page == 0) newProducts else state.productsByCategory + newProducts,
                                categoryProductsLoading = false,
                                categoryProductsError = null,
                                categoryProductsPage = page,
                                hasMoreCategoryProducts = hasMore
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { state ->
                            state.copy(
                                categoryProductsLoading = false,
                                categoryProductsError = result.message
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

    fun loadNextCategoryPage() {
        val currentState = _state.value
        if (!currentState.categoryProductsLoading && currentState.hasMoreCategoryProducts) {
            loadProductsByCategory(currentState.categoryId, currentState.categoryProductsPage + 1, 20)
        }
    }
    fun refreshCategoryProducts() {
        loadProductsByCategory(_state.value.categoryId, 0, 20, true)
    }

    fun refreshProducts() {
        loadBestSellerProducts(refresh = true)
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun loadFlashSale() {
        viewModelScope.launch {
            flashSaleRepository.getFlashSale().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val flashSales = result.data ?: emptyList()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                flashSales = flashSales,
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
}