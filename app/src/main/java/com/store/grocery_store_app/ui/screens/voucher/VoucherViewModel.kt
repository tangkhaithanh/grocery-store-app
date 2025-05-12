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

    private val _selectedVoucherIds = MutableStateFlow<Map<String, Long?>>(
        mapOf("FREESHIP" to null, "DISCOUNT" to null)
    )
    val selectedVoucherIds: StateFlow<Map<String, Long?>> = _selectedVoucherIds.asStateFlow()

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
        val current = _selectedVoucherIds.value.toMutableMap()
        val key = voucher.type ?: return

        current[key] = if (current[key] == voucher.id) null else voucher.id
        _selectedVoucherIds.value = current
    }
}
