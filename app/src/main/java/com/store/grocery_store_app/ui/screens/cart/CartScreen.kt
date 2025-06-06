package com.store.grocery_store_app.ui.screens.cart

import EmptyCategoryView
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.ErrorDialog
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.ui.components.SuccessDialog
import com.store.grocery_store_app.ui.screens.cart.components.CartItemCard
import com.store.grocery_store_app.ui.screens.checkout.Product
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onHome: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    onPayment: (List<Product>) -> Unit = {},
    onNavigateVoucher: () -> Unit = {},
    onNavigateToProductDetails: (Long) -> Unit = {}
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    val state by cartViewModel.state.collectAsState()
    val cartItems = state.cartItems
    val isCartChecked = cartViewModel.isCartChecked.value
    val itemCheckedMap = cartViewModel.itemCheckedMap
    var isInitiallySwiped by mutableStateOf(false)
    val totalPrice = cartItems
        .filter { itemCheckedMap[it.id ?: -1L] == true }
        .sumOf { it.price.toInt() * it.quantity }

    LaunchedEffect(Unit) {
        cartViewModel.getAllCartItem()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Giỏ hàng (${cartItems.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onHome) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isInitiallySwiped = !isInitiallySwiped
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Voucher Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.ConfirmationNumber,
                                contentDescription = "Ticket voucher",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Grocery Voucher",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.alpha(0.6f).clickable {
                                onNavigateVoucher()
                            }
                        ) {
                            Text(
                                text = "Xem mã đang có",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        thickness = 1.dp
                    )

                    // Checkout Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isCartChecked,
                                onCheckedChange = {
                                    cartViewModel.updateCartChecked(it)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .size(24.dp)
                            )
                            Text(
                                text = "Chọn tất cả",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Tổng cộng",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = currencyFormatter.format(totalPrice),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    var flag: Boolean = false;
                                    val selectedProducts = cartItems
                                        .filter { itemCheckedMap[it.id ?: -1L] == true }
                                        .map {
                                            if(it.price < 0.toBigDecimal()) flag = true
                                            Log.d("CartItem Price: ", it.price.toInt().toString())
                                            Product(
                                                id = it.product.id,
                                                name = it.product.name,
                                                price = it.price.setScale(0, RoundingMode.HALF_UP).toInt(),
                                                imageUrl = if (it.product.imageUrls.isNotEmpty()) {
                                                    it.product.imageUrls[0]
                                                } else {
                                                    "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75"
                                                },
                                                quantity = it.quantity,
                                                flashSaleItemId = it.flashSaleId
                                            )
                                        }
                                    if(flag) {
                                        cartViewModel.showError("Sản phẩm Flash Sale của bạn đã quá hạn.")
                                        return@Button
                                    }
                                    if(selectedProducts.isEmpty()) {
                                        cartViewModel.showError("Vui lòng chọn sản phẩm trước khi thanh toán")
                                    }
                                    else {
                                        onPayment(selectedProducts) // Gọi hàm điều hướng đã truyền vào
                                    }
                                },
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .wrapContentWidth()
                            ) {
                                Text(
                                    text = "Mua hàng (${itemCheckedMap.count{ it.value }})",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LoadingDialog(
            isLoading = state.isLoading,
            message = "Đang tải dữ liệu",
        )
        if(state.error!=null) {
            ErrorDialog(
                title = "Lỗi",
                content = state.error!!,
                clearError = cartViewModel::clearError
            )
        }
        if(state.isRemove) {
            SuccessDialog(
                title = "Xóa sản phẩm ra khỏi giỏ hàng",
                content = "Bạn đã thành công xóa sản phẩm ra khỏi giỏ hàng",
                clearError = cartViewModel::clearError,
                confirmButtonRequest =cartViewModel::onHideSuccess
            )
        }
        LazyColumn (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color(0xFFEAEAEA))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(cartItems.isNotEmpty()) {
                items(cartItems) { item ->
                    val checked = itemCheckedMap[item.id ?: -1L] == true
                    CartItemCard(
                        item,
                        checked = checked,
                        isInitiallySwiped = isInitiallySwiped,
                        onCheckedChange = { isChecked ->
                            cartViewModel.updateItemChecked(item.id, isChecked)
                        },
                        onQuantityIncrease = {
                            val currentQuantity = item.quantity ?: 0
                            val newQuantity = currentQuantity + 1
                            // Chỉ gọi nếu itemId hợp lệ
                            if (item.id != -1L) {
                                item.id?.let { cartViewModel.updateCartItemQuantity(it, newQuantity, item.product.id) }
                            }
                        },
                        onQuantityDecrease = {
                            val currentQuantity = item.quantity ?: 0
                            if (currentQuantity > 1) { // Giới hạn số lượng tối thiểu
                                val newQuantity = currentQuantity - 1
                                if (item.id != -1L) {
                                    item.id?.let { cartViewModel.updateCartItemQuantity(it, newQuantity, item.product.id) }
                                }
                            }
                        },
                        onRemove = {
                            if(item.id != null) cartViewModel.removeCartItem(item.id)
                        },
                        onNavigateToProductDetails = onNavigateToProductDetails
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
            else {
                item {
                    EmptyCategoryView(
                        content = "Bạn chưa có sản phẩm nào trong giỏ hàng"
                    )
                }
            }


        }
    }
}


@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    CartScreen({})
}