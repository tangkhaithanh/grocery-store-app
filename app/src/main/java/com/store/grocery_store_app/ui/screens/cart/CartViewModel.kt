package com.store.grocery_store_app.ui.screens.cart

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.OrderItem
import com.store.grocery_store_app.data.models.request.CartItemRequest
import com.store.grocery_store_app.data.models.request.ProductSimpleRequest
import com.store.grocery_store_app.data.models.response.CartItemResponse
import com.store.grocery_store_app.data.models.response.CartResponse
import com.store.grocery_store_app.data.models.response.OrderResponse
import com.store.grocery_store_app.data.repository.CartRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class CartState(
    val carts: CartResponse? = null,
    val cartItems: List<CartItemResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    // Trạng thái checkbox toàn bộ cart
    var isCartChecked = mutableStateOf(false)
        private set

    // Trạng thái của từng item
    private val _itemCheckedMap = mutableStateMapOf<Long, Boolean>()
    val itemCheckedMap: Map<Long, Boolean> = _itemCheckedMap

    fun getAllCartItem() {
        viewModelScope.launch {
            cartRepository.getAllCartItem().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val items = result.data?.cartItems ?: emptyList()
                        // Cập nhật map checkbox mặc định là false
                        items.forEach { _itemCheckedMap[it.id ?: -1L] = false }
                        _state.update {
                            it.copy(
                                carts = result.data,
                                cartItems = items,
                                isSuccess = true,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
    fun insertProductIntoCart(idCart: Long?, flashSaleId: Long?, quantity:Int, priceCart: BigDecimal,
                              idProduct:Long, name: String, priceProduct: BigDecimal, imageUrl: String?) {
        viewModelScope.launch {
            val productSimpleRequest : ProductSimpleRequest = ProductSimpleRequest(
                id = idProduct,
                name = name,
                price = priceProduct,
                imageUrl = imageUrl
            )
            val cartItemRequest : CartItemRequest = CartItemRequest(
                id = idCart,
                flashSaleId = flashSaleId,
                quantity = quantity,
                price = priceCart,
                product = productSimpleRequest
            )
            cartRepository.insertProductIntoCart(cartItemRequest).collect {result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isSuccess = true,
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
        }
    }
    fun updateCartChecked(checked: Boolean) {
        isCartChecked.value = checked
        _state.value.cartItems.forEach { item ->
            val id = item.id ?: -1L
            _itemCheckedMap[id] = checked
        }
    }

    fun updateItemChecked(itemId: Long?, checked: Boolean) {
        val id = itemId ?: return
        _itemCheckedMap[id] = checked

        // Nếu tất cả item đều được check => cart cũng phải được check
        val allChecked = _state.value.cartItems.all { item ->
            val itemIdSafe = item.id ?: -1L
            _itemCheckedMap[itemIdSafe] == true
        }
        isCartChecked.value = allChecked
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    fun getSelectedProductIds(): List<Long> {
        return _state.value.cartItems
            .filter { cartItem ->
                val itemId = cartItem.id ?: return@filter false
                _itemCheckedMap[itemId] == true
            }
            .mapNotNull { it.product?.id }
    }

}
