package com.store.grocery_store_app.ui.screens.ProductDetails.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.store.grocery_store_app.ui.theme.Gray600

/**
 * Quantity information section
 */
@Composable
fun QuantityInfo(
    quantity: Int,
    soldCount: Int
) {
    val remaining = quantity - soldCount
    if (remaining > 0) {
        Text(
            text = "Còn lại $remaining sản phẩm",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray600
        )
    } else {
        Text(
            text = "Hết hàng",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}