package com.store.grocery_store_app.ui.screens.order

import CategoryErrorView
import EmptyCategoryView
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.OrderTab
import com.store.grocery_store_app.data.models.StatusOrderType
import com.store.grocery_store_app.data.models.response.OrderResponse
import com.store.grocery_store_app.ui.screens.order.components.OrderGroupCard
import com.store.grocery_store_app.ui.screens.order.components.OrderItemCard
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.utils.OrderUtil.groupOrders


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = hiltViewModel(),
    onHome: () -> Unit,
    onNavigateToReviewProduct: (Long, Long) -> Unit,
    onNavigateToProductDetails: (Long) -> Unit,
    onNavigateToDeliveryDetail: (String) -> Unit
) {
    val orderState by orderViewModel.state.collectAsState()
    val isLoading = orderState.isLoading
    val error = orderState.error
    val selectedTabIndex = orderState.selectedTabIndex
    val tabs = listOf(
        OrderTab("Chờ xác nhận", StatusOrderType.PENDING),
        OrderTab("Chờ lấy hàng", StatusOrderType.CONFIRMED),
        OrderTab("Chờ giao hàng", StatusOrderType.SHIPPED),
        OrderTab("Đã giao", StatusOrderType.DELIVERED),
        OrderTab("Đã huỷ", StatusOrderType.CANCELED)
    )
    val orderItems = orderState.orderItems
    val orders = orderState.orders

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
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp
            ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        orderViewModel.setSelectedTabIndex(index)
                        orderViewModel.loadOrders(tab.status)
                    }
                ) {
                    Text(
                        text = tab.title,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex == index) Color.Red else Color.Black
                    )
                }
            }
        }

        val isDelivered = tabs[selectedTabIndex].title == "Đã giao"

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
                orderItems.isNotEmpty() -> {
                    // Categories row with adjusted spacing
                    if (isDelivered) {

                        items(orderItems) { orderItem ->
                            OrderItemCard(
                                orderItem = orderItem,
                                onNavigateToReviewProduct
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    } else {
                        val grouped = groupOrders(orderItems)
                        items(grouped) { group ->
                            OrderGroupCard(group, tabs[selectedTabIndex].title, onNavigateToProductDetails, onNavigateToDeliveryDetail)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                else -> {
                    item { EmptyCategoryView(
                        content = "Bạn chưa có đơn hàng nào!!!"
                    ) }
                }
            }

        }

    }
}






@Preview(showBackground = true)
@Composable
fun OrderScreenPreview() {

}