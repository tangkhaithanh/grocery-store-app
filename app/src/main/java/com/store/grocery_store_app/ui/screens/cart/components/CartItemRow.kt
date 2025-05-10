package com.store.grocery_store_app.ui.screens.cart.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.store.grocery_store_app.data.models.request.CartItemRequest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt



@Composable
fun CartItemRow(
    cartItem : CartItemRequest,
    checked : Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    var quantity by remember { mutableStateOf(1) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
            }
        )
        AsyncImage(
            model = "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75",
            contentDescription = "",
            modifier = Modifier
                .size(56.dp)
        )
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = "San pham",
                maxLines = 1,
                fontSize = 16.sp,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween    // <-- tự đẩy 2 phần tử ra 2 đầu

            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(currencyFormatter.format(cartItem.price), fontWeight = FontWeight.Medium,
                        color = Color.Red)
                    Spacer(modifier = Modifier.width(10.dp))
                    if (cartItem.price != cartItem.product.price) {
                        Text(
                            text = currencyFormatter.format(cartItem.product.price),
                            style = TextStyle(textDecoration = TextDecoration.LineThrough),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                QuantityPicker(
                    quantity = quantity,
                    quantityMax = 10,
                    size = 24.dp,
                    onQuantityChange = { newQty -> quantity = newQty },
                    modifier = Modifier.padding(6.dp)
                )
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(size)
            .width(size * 3)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút giảm
        Box(
            modifier = Modifier
                .width(size)
                .fillMaxHeight()
                .clickable(enabled = quantity > 1) {
                    onQuantityChange((quantity - 1).coerceAtLeast(1))
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                modifier = Modifier.width(size * 0.5f)
            )
        }

        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        // Giá trị hiện tại
        Box(
            modifier = Modifier
                .width(size)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Divider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        // Nút tăng
        Box(
            modifier = Modifier
                .width(size)
                .fillMaxHeight()
                .clickable(enabled = quantity < quantityMax) {
                    onQuantityChange((quantity + 1).coerceAtMost(quantityMax))
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                modifier = Modifier.width(size * 0.5f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableCartItemRow(
    cartItem: CartItemRequest,
    checked: Boolean = false,
    onDelete: () -> Unit,
    onShowSimilar: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            it == DismissValue.Default || it == DismissValue.DismissedToStart
        }
    )

    val isRevealed = dismissState.currentValue == DismissValue.DismissedToStart

    val colorSimilar by animateColorAsState(
        if (isRevealed) Color(0xFFFF9800) else Color.Transparent,
        animationSpec = tween(300), label = "similar"
    )
    val colorDelete by animateColorAsState(
        if (isRevealed) Color(0xFFF44336) else Color.Transparent,
        animationSpec = tween(300), label = "delete"
    )
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(
            DismissDirection.EndToStart,
            DismissDirection.StartToEnd
        ),
        background = {
            if (isRevealed) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White),
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
                        TextButton(
                            onClick = { onShowSimilar() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Text("Sản phẩm\n  tương tự", textAlign = TextAlign.Center)
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(80.dp)
                            .background(colorDelete),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(
                            onClick = { onDelete() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Text("Xóa")
                        }
                    }
                }
            }
        },
        dismissContent = {
            CartItemRow(
                cartItem = cartItem,
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.padding(vertical = 4.dp)
    )
}





@Preview
@Composable
fun QuantityPickerPreview() {
    QuantityPicker(
        quantity = 1,
        quantityMax = 10,
        onQuantityChange = {}
    )
}