package com.store.grocery_store_app.ui.screens.search
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

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val products: List<ProductResponse> = emptyList(),
    val error: String? = null,
    val page: Int = 0,
    val hasMoreItems: Boolean = true,
    val isEmpty: Boolean = false,
    val hasSearched: Boolean = false
)
@HiltViewModel
class SearchViewModel @Inject constructor(private val productRepository: ProductRepository):
    ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun search(query: String, refresh: Boolean = true) {
        if (query.isBlank()) {
            _state.update {
                it.copy(
                    products = emptyList(),
                    isLoading = false,
                    isEmpty = false,
                    error = null,
                    hasSearched = true
                )
            }
            return
        }

        viewModelScope.launch {
            if (refresh) {
                _state.update {
                    it.copy(
                        isLoading = true,
                        products = emptyList(),
                        page = 0,
                        hasMoreItems = true,
                        error = null,
                        isEmpty = false,
                        hasSearched = true
                    )
                }
            } else {
                _state.update { it.copy(isLoading = true, error = null) }
            }

            productRepository.searchProducts(
                query = query,
                page = if (refresh) 0 else _state.value.page,
                size = 20
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val newProducts = result.data ?: emptyList()
                        val allProducts = if (refresh) {
                            newProducts
                        } else {
                            _state.value.products + newProducts
                        }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                products = allProducts,
                                page = it.page + 1,
                                hasMoreItems = newProducts.isNotEmpty(),
                                isEmpty = allProducts.isEmpty(),
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                                isEmpty = it.products.isEmpty()
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

    fun loadMoreProducts() {
        if (!_state.value.isLoading && _state.value.hasMoreItems) {
            search(_state.value.query, false)
        }
    }

    fun clearResults() {
        _state.update {
            it.copy(
                products = emptyList(),
                isLoading = false,
                page = 0,
                hasMoreItems = true,
                isEmpty = false,
                error = null,
                hasSearched = false
            )
        }
    }
}