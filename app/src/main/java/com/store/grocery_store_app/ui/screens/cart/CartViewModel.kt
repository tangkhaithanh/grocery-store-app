package com.store.grocery_store_app.ui.screens.cart

import android.util.Log
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val isSuccess: Boolean = false,
    val isRemove: Boolean = false
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
    // --- START: Thêm cho Debounce ---
    private val updateQuantityDebounceJobs = mutableMapOf<Long, Job>()
    private val debounceDelayMillis = 3000L // 3 giây
    // --- END: Thêm cho Debounce ---
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
    fun showError(message : String) {
        _state.update { it.copy(error = message) }
    }
    fun getSelectedProductIds(): List<Long> {
        return _state.value.cartItems
            .filter { cartItem ->
                val itemId = cartItem.id ?: return@filter false
                _itemCheckedMap[itemId] == true
            }
            .mapNotNull { it.product?.id }
    }

    fun removeCartItem(id:Long) {
        viewModelScope.launch {
            cartRepository.removeCartItem(id).collect { result ->
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
                        getAllCartItem()
                        _state.update {
                            it.copy(
                                isRemove = true,
                                isLoading = false,
                                error = null,
                                isSuccess = false
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
                }            }
        }

    }
    fun onHideSuccess() {
        _state.update { it.copy(isRemove = false, isSuccess = false) }
    }
    // --- START: Hàm cập nhật số lượng với Debounce ---
    fun updateCartItemQuantity(cartItemId: Long, newQuantity: Int, productId: Long) {

        _state.update { currentState ->
            val updatedItems = currentState.cartItems.map {
                if (it.id == cartItemId) {
                    it.copy(quantity = newQuantity.coerceAtLeast(1)) // Đảm bảo số lượng > 0
                } else {
                    it
                }
            }
            currentState.copy(cartItems = updatedItems)
        }

        // 2. Hủy bỏ job debounce cũ nếu có cho cartItemId này
        updateQuantityDebounceJobs[cartItemId]?.cancel()

        // 3. Tạo job debounce mới
        updateQuantityDebounceJobs[cartItemId] = viewModelScope.launch {
            delay(debounceDelayMillis)

            // Sau khi delay, thực hiện gọi API
            Log.d("CartViewModel_UpdateQuantity", "Debounced: Gọi API để cập nhật cartItem $cartItemId với số lượng $newQuantity")
            val cartItemRequest:CartItemRequest = CartItemRequest(
                id = cartItemId,
                quantity = newQuantity,
                flashSaleId = null,
                price = 0.0.toBigDecimal(),
                product = ProductSimpleRequest(
                    id = productId,
                    name = "",
                    price = BigDecimal.ZERO,
                    imageUrl = ""
                )
            )
             cartRepository.updateProductIntoCart(cartItemRequest).collect { result ->
                 when (result) {
                     is Resource.Loading -> {
                         _state.update { it.copy(isLoading = true) } // Có thể hiển thị loading nếu muốn
                     }
                     is Resource.Success -> {
                         Log.d("CartViewModel_UpdateQuantity", "API Success: Cập nhật thành công cartItem $cartItemId")
                         getAllCartItem()
                         _state.update { it.copy(isLoading = false, error = null) }
                     }
                     is Resource.Error -> {
                         Log.e("CartViewModel_UpdateQuantity", "API Error: Lỗi cập nhật cartItem $cartItemId - ${result.message}")
                         _state.update { it.copy(isLoading = false, error = result.message ?: "Lỗi cập nhật số lượng") }
                     }
                 }
             }
            updateQuantityDebounceJobs.remove(cartItemId)
        }
    }
    // --- START: Thêm để dọn dẹp Job khi ViewModel bị hủy ---
    override fun onCleared() {
        super.onCleared()
        updateQuantityDebounceJobs.values.forEach { it.cancel() } // Hủy tất cả các job debounce còn lại
        updateQuantityDebounceJobs.clear()
    }
    // --- END: Thêm để dọn dẹp Job khi ViewModel bị hủy ---
}
