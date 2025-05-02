package com.store.grocery_store_app.ui.screens.order

import CategoryErrorView
import CategoryLoadingIndicator
import EmptyCategoryView
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.R
import com.store.grocery_store_app.data.models.OrderItem
import com.store.grocery_store_app.data.models.OrderTab
import com.store.grocery_store_app.data.models.StatusOrderType
import com.store.grocery_store_app.ui.screens.order.components.OrderGroupCard
import com.store.grocery_store_app.ui.screens.order.components.OrderItemCard
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.utils.OrderUtil.groupOrders


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = hiltViewModel(),
    onHome: () -> Unit
) {
    val orderState by orderViewModel.state.collectAsState()
    val orders = orderState.orders
    val isLoading = orderState.isLoading
    val error = orderState.error
    val selectedTabIndex = remember { mutableStateOf(0) }
    val tabs = listOf(
        OrderTab("Chờ xác nhận", StatusOrderType.PENDING),
        OrderTab("Chờ lấy hàng", StatusOrderType.CONFIRMED),
        OrderTab("Chờ giao hàng", StatusOrderType.SHIPPED),
        OrderTab("Đã giao", StatusOrderType.DELIVERED),
        OrderTab("Đã huỷ", StatusOrderType.CANCELED)
    )
    var sampleOrders by remember { mutableStateOf<List<OrderItem>>(emptyList()) }
    LaunchedEffect(orders) {
        Log.d("TestOrders", "Số lượng đơn hàng: ${orders.size}")
        val resultList = mutableListOf<OrderItem>()
        orders.forEach { order ->
            order.orderItems.forEach { orderItem ->
                resultList.add(
                    OrderItem(
                        orderId = order.id.toString(),
                        storeName = "Grocery Store",
                        productName = orderItem.product.name,
                        productDescription = orderItem.product.description,
                        imageRes = R.drawable.ic_package, // Replace with actual image if available
                        quantity = orderItem.quantity,
                        sellPrice = orderItem.product.price,
                        buyPrice = orderItem.price,
                        totalAmount = (orderItem.price * orderItem.quantity.toBigDecimal()).toInt(),
                        orderItem.reviewed,
                        orderItem.canReview
                    )
                )
            }
        }
        sampleOrders = resultList
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 16.dp)
    ) {
        TopAppBar(
            title = { Text("Đơn đã mua") },
            navigationIcon = {
                IconButton(onClick = { onHome() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex.value,
            edgePadding = 0.dp
            ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = {
                        selectedTabIndex.value = index
                        orderViewModel.loadOrders(tab.status)
                    }
                ) {
                    Text(
                        text = tab.title,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = if (selectedTabIndex.value == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex.value == index) Color.Red else Color.Black
                    )
                }
            }
        }

        val isDelivered = tabs[selectedTabIndex.value].title == "Đã giao"

        LazyColumn(modifier = Modifier.padding(8.dp)) {
            when {
                isLoading -> {
                    item {  CircularProgressIndicator(color = DeepTeal) }
                }
                error != null -> {
                    item {
                        CategoryErrorView(error = error) {
                            orderViewModel.loadOrders()
                        }
                    }
                }
                sampleOrders.isNotEmpty() -> {
                    // Categories row with adjusted spacing
                    if (isDelivered) {

                        items(sampleOrders) { order ->
                            OrderItemCard(order)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        val grouped = groupOrders(sampleOrders)
                        items(grouped) { group ->
                            OrderGroupCard(group, tabs[selectedTabIndex.value].title)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                else -> {
                    item { EmptyCategoryView() }
                }
            }

        }

    }
}






@Preview(showBackground = true)
@Composable
fun OrderScreenPreview() {
    OrderScreen(onHome = {})
}