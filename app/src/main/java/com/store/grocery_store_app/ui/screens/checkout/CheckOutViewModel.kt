package com.store.grocery_store_app.ui.screens.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.request.CreateOrderRequest
import com.store.grocery_store_app.data.models.request.OrderItemRequest
import com.store.grocery_store_app.data.models.response.AddressDTO
import com.store.grocery_store_app.data.models.response.CreateOrderResponse
import com.store.grocery_store_app.data.models.response.VoucherResponse
import com.store.grocery_store_app.data.repository.AddressRepository
import com.store.grocery_store_app.data.repository.OrderRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _defaultAddress = MutableStateFlow<AddressDTO?>(null)
    val defaultAddress: StateFlow<AddressDTO?> = _defaultAddress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _orderSuccess = MutableStateFlow<CreateOrderResponse?>(null)
    val orderSuccess: StateFlow<CreateOrderResponse?> = _orderSuccess.asStateFlow()


    init {
        loadDefaultAddress()
    }

    private fun loadDefaultAddress() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            addressRepository.getAllAddresses(0, 10).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Tìm địa chỉ mặc định trong danh sách
                        val defaultAddress = result.data?.find { it.isDefault }
                        // Nếu không có địa chỉ mặc định, lấy địa chỉ đầu tiên (nếu có)
                        val addressToUse = defaultAddress ?: result.data?.firstOrNull()

                        _defaultAddress.value = addressToUse
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        // Đã xử lý ở trên
                    }
                }
            }
        }
    }

    fun loadAddressById(addressId: Long, callback: (AddressDTO?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            addressRepository.getAddressById(addressId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        callback(result.data)
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                        callback(null)
                    }
                    is Resource.Loading -> {
                        // Đã xử lý ở trên
                    }
                }
            }
        }
    }

    fun createOrder(
        products: List<Product>,
        selectedAddress: AddressDTO?,
        voucher: VoucherResponse?,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Validate address
                if (selectedAddress == null) {
                    _error.value = "Vui lòng chọn địa chỉ giao hàng"
                    _isLoading.value = false
                    return@launch
                }

                // Tạo order items với giá từ product
                val orderItems = products.map { product ->
                    OrderItemRequest(
                        productId = product.id,
                        flashSaleItemId = product.flashSaleItemId,
                        quantity = product.quantity,
                        price = BigDecimal.valueOf(product.price.toDouble())
                    )
                }

                // Tạo request KHÔNG có totalAmount
                val createOrderRequest = CreateOrderRequest(
                    addressId = selectedAddress.id,
                    voucherId = voucher?.id,
                    paymentMethod = paymentMethod,
                    orderItems = orderItems
                )

                Log.d("CheckoutViewModel", "Creating order: $createOrderRequest")

                // Gọi API
                orderRepository.createOrder(createOrderRequest).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("CheckoutViewModel", "Order created successfully: ${result.data}")
                            _orderSuccess.value = result.data
                            _isLoading.value = false
                        }
                        is Resource.Error -> {
                            Log.e("CheckoutViewModel", "Error creating order: ${result.message}")
                            _error.value = result.message
                            _isLoading.value = false
                        }
                        is Resource.Loading -> {
                            // Đã xử lý ở trên
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Exception creating order", e)
                _error.value = "Lỗi khi tạo đơn hàng: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun clearOrderSuccess() {
        _orderSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun showError(message: String) {
        _error.value = message
    }
}