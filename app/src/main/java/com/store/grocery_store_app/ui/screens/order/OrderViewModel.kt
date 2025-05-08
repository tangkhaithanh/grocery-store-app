package com.store.grocery_store_app.ui.screens.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.OrderItem
import com.store.grocery_store_app.data.models.StatusOrderType
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

data class OrderState(
    val orders: List<OrderResponse> = emptyList(),
    val orderItems: List<OrderItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTabIndex: Int = 0
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel()
{
    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state.asStateFlow()
    init{
        loadOrders(StatusOrderType.PENDING)
    }
    fun setSelectedTabIndex(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
    }

    fun loadOrders(status: StatusOrderType = StatusOrderType.ALL) {
        Log.d("Get Data: ", status.toString())
        viewModelScope.launch {
            orderRepository.getOrders(typeStatusOrder = status).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val orders = result.data ?: emptyList()
                        val items = mapToOrderItems(orders)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                orders = orders,
                                orderItems = items,
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

    private fun mapToOrderItems(orders: List<OrderResponse>): List<OrderItem> {
        val resultList = mutableListOf<OrderItem>()
        orders.forEach { order ->
            order.orderItems.forEach { orderItem ->
                resultList.add(
                    OrderItem(
                        orderId = order.id.toString(),
                        orderItemId = orderItem.id.toString(),
                        productId = orderItem.product.id,
                        storeName = "Grocery Store",
                        productName = orderItem.product.name,
                        productDescription = orderItem.product.description,
                        imageRes = "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75",
                        quantity = orderItem.quantity,
                        sellPrice = orderItem.product.price,
                        buyPrice = orderItem.price,
                        totalAmount = (orderItem.price * orderItem.quantity.toBigDecimal()).toInt(),
                        canReview = orderItem.canReview,
                        reviewed = orderItem.reviewed
                    )
                )
            }
        }
        return resultList
    }


}