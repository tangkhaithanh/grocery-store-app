package com.store.grocery_store_app.ui.screens.FavoriteProduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.local.TokenManager
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.data.repository.FavoriteProductRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

data class FavouriteState(
    val isLoading: Boolean = false,
    val favouriteProducts: List<ProductResponse> = emptyList(),
    val error: String? = null,
    val showLoginRequired: Boolean = false
)
@HiltViewModel
class FavoriteProductViewModel @Inject constructor(private val favouriteRepository: FavoriteProductRepository,
                                                   private val tokenManager: TokenManager
) : ViewModel(){
    private val _state = MutableStateFlow(FavouriteState())
    val state: StateFlow<FavouriteState> = _state.asStateFlow()

    init {
        // Tải danh sách sản phẩm yêu thích khi khởi tạo ViewModel
        if (isUserLoggedIn()) {
            loadFavouriteProducts()
        }
    }
    private fun isUserLoggedIn(): Boolean {
        val accessToken = runBlocking { tokenManager.accessToken.first() }
        return !accessToken.isNullOrEmpty()
    }

    fun loadFavouriteProducts() {
        if (!isUserLoggedIn()) {
            return
        }

        viewModelScope.launch {
            favouriteRepository.getFavouriteProducts().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                favouriteProducts = result.data ?: emptyList(),
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

    // Hàm kiểm tra sản phẩm có trong danh sách yêu thích không
    fun isFavourite(productId: Long): Flow<Boolean> {
        return if (isUserLoggedIn()) {
            favouriteRepository.isFavourite(productId)
        } else {
            flowOf(false)
        }
    }

    // Hàm chuyển đổi trạng thái yêu thích
    fun toggleFavourite(productId: Long) {
        if (!isUserLoggedIn()) {
            _state.update { it.copy(showLoginRequired = true) }
            return
        }

        viewModelScope.launch {
            val isFavourite = favouriteRepository.isFavourite(productId).first()

            if (isFavourite) {
                // Xóa khỏi danh sách yêu thích
                favouriteRepository.removeFromFavourite(productId).collect { result ->
                    handleToggleResult(result)
                }
            } else {
                // Thêm vào danh sách yêu thích
                favouriteRepository.addToFavourite(productId).collect { result ->
                    handleToggleResult(result)
                }
            }
        }
    }

    private fun handleToggleResult(result: Resource<Boolean>) {
        when (result) {
            is Resource.Loading -> {
                _state.update { it.copy(isLoading = true) }
            }
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
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

    fun clearLoginRequiredMessage() {
        _state.update { it.copy(showLoginRequired = false) }
    }

}