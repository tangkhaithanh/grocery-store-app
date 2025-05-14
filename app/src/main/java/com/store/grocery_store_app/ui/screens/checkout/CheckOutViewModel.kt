package com.store.grocery_store_app.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.response.AddressDTO
import com.store.grocery_store_app.data.repository.AddressRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _defaultAddress = MutableStateFlow<AddressDTO?>(null)
    val defaultAddress: StateFlow<AddressDTO?> = _defaultAddress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
}