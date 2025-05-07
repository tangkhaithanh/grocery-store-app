package com.store.grocery_store_app.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Nút giỏ hàng (48 dp) kèm badge số lượng.
 * Badge nằm **ngoài** IconButton ⇒ không bị cắt bởi clip hình tròn.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartButton(
    itemCount: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.White          //  <-- mới
) {
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.matchParentSize()
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                tint = iconTint,           //  <-- dùng màu truyền vào
                modifier = Modifier.size(24.dp)
            )
        }

        if (itemCount != null && itemCount > 0) {
            Badge(
                containerColor = Color.Red,
                contentColor = Color.White,
                modifier = Modifier.offset(x = (-2).dp, y = 2.dp)
            ) {
                Text(
                    text = itemCount.toString(),
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

