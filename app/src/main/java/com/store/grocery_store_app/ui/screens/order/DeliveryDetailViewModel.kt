package com.store.grocery_store_app.ui.screens.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.store.grocery_store_app.data.models.OrderItem
import com.store.grocery_store_app.data.models.response.OrderResponse
import com.store.grocery_store_app.data.repository.OrderRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliveryOrderState(
    val order: OrderResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
@HiltViewModel
class DeliveryDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DeliveryOrderState())
    val state: StateFlow<DeliveryOrderState> = _state.asStateFlow()

    fun getOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrder(orderId.toLong()).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }

                    is Resource.Success -> {
                        val order = result.data
                        _state.update {
                            it.copy(
                                isLoading = false,
                                order = order,
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