package com.store.grocery_store_app.ui.screens.cart.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.store.grocery_store_app.data.models.request.CartItemRequest
import com.store.grocery_store_app.data.models.response.CartItemResponse
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.store.grocery_store_app.ui.components.ErrorDialog
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
@Composable
fun CartItemRow(
    cartItem: CartItemResponse,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onNavigateToProductDetails: (Long) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    var quantity by remember { mutableStateOf(cartItem.quantity) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        if(cartItem.product.imageUrls.isNotEmpty()) {
            if(cartItem.price < 0.toBigDecimal()) {
                AsyncImage(
                    model = cartItem.product.imageUrls[0],
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                        .background(Color(0xFFFAE3E0))
                        .clickable {
                            onNavigateToProductDetails(cartItem.product.id)
                        }
                )
            }
            else {
                AsyncImage(
                    model = cartItem.product.imageUrls[0],
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                        .clickable {
                            onNavigateToProductDetails(cartItem.product.id)
                        }
                )
            }

        }
        else {
            if(cartItem.price < 0.toBigDecimal()) {
                AsyncImage(
                    model = "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75",
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                        .background(Color(0xFFFAE3E0))
                        .clickable {
                            onNavigateToProductDetails(cartItem.product.id)
                        }
                )
            }
            else {
                AsyncImage(
                    model = "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75",
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                        .clickable {
                            onNavigateToProductDetails(cartItem.product.id)
                        }
                )
            }

        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = cartItem.product.name, maxLines = 1, style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(currencyFormatter.format(cartItem.price), style = MaterialTheme.typography.bodyLarge, color = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (cartItem.price != cartItem.product.price) {
                        Text(
                            text = currencyFormatter.format(cartItem.product.price),
                            style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                            color = Color.Gray
                        )
                    }
                }
                if(cartItem.price >= 0.toBigDecimal()) {
                    QuantityPicker(
                        quantity = quantity,
                        quantityMax = cartItem.product.quantity-cartItem.product.soldCount,
                        onQuantityChange = {
                            quantity = it
                        },
                        onQuantityIncrease = onQuantityIncrease,
                        onQuantityDecrease = onQuantityDecrease,
                    )
                } else {
                    QuantityPicker(
                        quantity = quantity,
                        quantityMax = cartItem.product.quantity-cartItem.product.soldCount,
                        onQuantityChange = {},
                        onQuantityIncrease = {},
                        onQuantityDecrease = {},
                    )
                }

            }
        }
    }
}

@Composable
fun QuantityPicker(
    quantity: Int,
    quantityMax: Int,
    size: Dp = 24.dp,
    onQuantityChange: (Int) -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
) {
    if(quantity == 0) ErrorDialog(
        title = "Lỗi",
        content = "Không được chọn số lượng sản phẩm dưới 1",
    )
    else if(quantity>quantityMax) ErrorDialog(
        title = "Lỗi",
        content = "Không được chọn số lượng sản phẩm vượt quá số lượng sản phẩm trong kho",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(size)
            .width(size * 3)
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
    ) {
        IconButton(
            onClick = {
                onQuantityChange((quantity - 1).coerceAtLeast(1))
                      onQuantityDecrease()},
            enabled = quantity > 1,
            modifier = Modifier.size(size)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(size * 0.5f))
        }
        Divider(Modifier
            .width(1.dp)
            .fillMaxHeight())
        Box(modifier = Modifier
            .width(size)
            .fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(text = quantity.toString(), style = MaterialTheme.typography.titleMedium)
        }
        Divider(Modifier
            .width(1.dp)
            .fillMaxHeight())
        IconButton(
            onClick = {
                onQuantityChange((quantity + 1).coerceAtMost(quantityMax))
                onQuantityIncrease()
                      },
            enabled = quantity < quantityMax,
            modifier = Modifier.size(size)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(size * 0.5f))
        }
    }
}

@Composable
fun SwipeableCartItemRow(
    cartItem: CartItemResponse,
    checked: Boolean = false,
    isInitiallySwiped: Boolean = false,
    onDelete: (Long) -> Unit,
    onShowSimilar: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onNavigateToProductDetails: (Long) -> Unit
) {
    var actionWidth by remember { mutableStateOf(0f) }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isExpanded by remember { mutableStateOf(false) }
    // Pre-calc gap in px
    val gapPx = with(LocalDensity.current) { 12.dp.toPx() }
    var showConfirmDialog by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = isInitiallySwiped, key2 = actionWidth) {
        if (isInitiallySwiped && actionWidth > 0f && !isExpanded) { // Chỉ swipe nếu được yêu cầu, actionWidth đã có và chưa swipe
            scope.launch {
                offsetX.animateTo(-actionWidth, tween(300))
                isExpanded = true
            }
        } else if (!isInitiallySwiped && isExpanded) { // Nếu isInitiallySwiped = false và đang swipe, đóng lại
            scope.launch {
                offsetX.animateTo(0f, tween(300))
                isExpanded = false
            }
        }
    }
    // Confirmation dialog for delete
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa sản phẩm này ra khỏi giỏ hàng?") },
            confirmButton = {
                TextButton(onClick = {
                    cartItem.id?.let { onDelete(it) }
                    showConfirmDialog = false
                }) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .pointerInput(isExpanded, actionWidth) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch {
                            val raw = offsetX.value + dragAmount
                            val coerced = raw.coerceIn(-actionWidth, 0f)
                            offsetX.snapTo(coerced)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            if (!isExpanded) {
                                if (offsetX.value <= -actionWidth / 2) {
                                    offsetX.animateTo(-actionWidth, tween(300))
                                    isExpanded = true
                                } else {
                                    offsetX.animateTo(0f, tween(300))
                                }
                            } else {
                                if (offsetX.value >= -actionWidth / 2) {
                                    offsetX.animateTo(0f, tween(300))
                                    isExpanded = false
                                } else {
                                    offsetX.animateTo(-actionWidth, tween(300))
                                }
                            }
                        }
                    }
                )
            }
    ) {
        // Actions container: slide together with content drag
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                Modifier
                    .wrapContentWidth()
                    .onSizeChanged { actionWidth = it.width.toFloat() }
                    .offset {
                        // offset so action row enters with a gap
                        IntOffset((offsetX.value + actionWidth + gapPx).roundToInt(), 0)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(60.dp)
                        .background(MaterialTheme.colorScheme.error)
                        .clickable {
                            showConfirmDialog = true;
                        },
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { showConfirmDialog = true }) {
                        Text("Xóa",
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Foreground content
        Box(
            Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        ) {
            CartItemRow(
                cartItem = cartItem,
                checked = checked,
                onCheckedChange = onCheckedChange,
                onQuantityIncrease = onQuantityIncrease,
                onQuantityDecrease = onQuantityDecrease,
                onNavigateToProductDetails = onNavigateToProductDetails
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun QuantityPickerPreview() {
//    QuantityPicker(quantity = 1, quantityMax = 10, onQuantityChange = {})
}