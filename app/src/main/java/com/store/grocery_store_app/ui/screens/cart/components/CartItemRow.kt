package com.store.grocery_store_app.ui.screens.cart.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun CartItemRow(
    cartItem: CartItemResponse,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
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
            AsyncImage(
                model = cartItem.product.imageUrls[0],
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        }
        else {
            AsyncImage(
                model = "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75",
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
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
                QuantityPicker(quantity = quantity, quantityMax = cartItem.product.quantity-cartItem.product.soldCount, onQuantityChange = {
                    quantity = it
                })
            }
        }
    }
}

@Composable
fun QuantityPicker(
    quantity: Int,
    quantityMax: Int,
    size: Dp = 24.dp,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(size)
            .width(size * 3)
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        IconButton(
            onClick = { onQuantityChange((quantity - 1).coerceAtLeast(1)) },
            enabled = quantity > 1,
            modifier = Modifier.size(size)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(size * 0.5f))
        }
        Divider(Modifier.width(1.dp).fillMaxHeight())
        Box(modifier = Modifier.width(size).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Text(text = quantity.toString(), style = MaterialTheme.typography.titleMedium)
        }
        Divider(Modifier.width(1.dp).fillMaxHeight())
        IconButton(
            onClick = { onQuantityChange((quantity + 1).coerceAtMost(quantityMax)) },
            enabled = quantity < quantityMax,
            modifier = Modifier.size(size)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(size * 0.5f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableCartItemRow(
    cartItem: CartItemResponse,
    checked: Boolean = false,
    onDelete: () -> Unit,
    onShowSimilar: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val state = rememberSwipeToDismissBoxState()
    val dismissed = state.dismissDirection
    val isRevealed = dismissed != SwipeToDismissBoxValue.Settled

    val colorSimilar by animateColorAsState(
        targetValue = if (isRevealed && dismissed == SwipeToDismissBoxValue.EndToStart) Color(0xFFFF9800) else Color.Transparent,
        animationSpec = tween(300)
    )
    val colorDelete by animateColorAsState(
        targetValue = if (isRevealed && dismissed == SwipeToDismissBoxValue.StartToEnd) Color(0xFFF44336) else Color.Transparent,
        animationSpec = tween(300)
    )

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            if (isRevealed) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(100.dp)
                            .background(colorSimilar),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = onShowSimilar) {
                            Text("Sản phẩm tương tự", textAlign = TextAlign.Center)
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(80.dp)
                            .background(colorDelete),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = onDelete) {
                            Text("Xóa")
                        }
                    }
                }
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        CartItemRow(
            cartItem = cartItem,
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuantityPickerPreview() {
    QuantityPicker(quantity = 1, quantityMax = 10, onQuantityChange = {})
}