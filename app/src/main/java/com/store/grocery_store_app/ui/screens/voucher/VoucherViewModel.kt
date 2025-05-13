package com.store.grocery_store_app.ui.screens.voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.response.VoucherResponse
import com.store.grocery_store_app.data.repository.VoucherRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VoucherUiState(
    val isLoading: Boolean = false,
    val vouchersFreeship: List<VoucherResponse> = emptyList(),
    val vouchersDiscount: List<VoucherResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VoucherViewModel @Inject constructor(
    private val repository: VoucherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoucherUiState())
    val uiState: StateFlow<VoucherUiState> = _uiState.asStateFlow()

    // Changed to store a single selected voucher ID instead of a map
    private val _selectedVoucherId = MutableStateFlow<Long?>(null)
    val selectedVoucherId: StateFlow<Long?> = _selectedVoucherId.asStateFlow()

    // Keep track of selected voucher type for easier access
    private val _selectedVoucherType = MutableStateFlow<String?>(null)
    val selectedVoucherType: StateFlow<String?> = _selectedVoucherType.asStateFlow()

    init {
        fetchVouchers()
    }

    private fun fetchVouchers() {
        viewModelScope.launch {
            repository.getAllVoucher().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        val data = result.data ?: emptyList()
                        val freeship = data.filter { it.type == "FREESHIP" }
                        val discount = data.filter { it.type == "DISCOUNT" }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                vouchersFreeship = freeship,
                                vouchersDiscount = discount,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Đã xảy ra lỗi"
                            )
                        }
                    }
                }
            }
        }
    }

    fun onVoucherChecked(voucher: VoucherResponse) {
        // If this voucher is already selected, unselect it
        if (_selectedVoucherId.value == voucher.id) {
            _selectedVoucherId.value = null
            _selectedVoucherType.value = null
        } else {
            // Otherwise, select this new voucher (replacing any previously selected one)
            _selectedVoucherId.value = voucher.id
            _selectedVoucherType.value = voucher.type
        }
    }

    // Get the currently selected voucher object (not just ID)
    fun getSelectedVoucher(): VoucherResponse? {
        val id = _selectedVoucherId.value ?: return null

        return when (_selectedVoucherType.value) {
            "FREESHIP" -> _uiState.value.vouchersFreeship.find { it.id == id }
            "DISCOUNT" -> _uiState.value.vouchersDiscount.find { it.id == id }
            else -> null
        }
    }

    // Check if a specific voucher is currently selected
    fun isVoucherSelected(voucherId: Long): Boolean {
        return _selectedVoucherId.value == voucherId
    }


    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    fun showError(message : String) {
        _uiState.update { it.copy(error = message) }
    }
}