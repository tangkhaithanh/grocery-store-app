package com.store.grocery_store_app.ui.screens.ProductDetails.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.store.grocery_store_app.ui.components.CustomButtonWithIcon

/**
 * Add to cart button component using CustomButtonWithIcon
 */
@Composable
fun AddToCartButton(
    remaining: Int,
    onAddToCart: () -> Unit = {},
    onShowSheet: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val buttonText = if (remaining > 0) "Thêm vào giỏ hàng" else "Hết hàng"

    CustomButtonWithIcon(
        text      = buttonText,
        onClick   = {
            onShowSheet()
            onAddToCart()
        },
        isLoading = isLoading,
        enabled   = remaining > 0,
        icon      = if (remaining > 0) Icons.Default.ShoppingCart else null,
        modifier  = modifier.fillMaxWidth()  // ⬅️ Dùng modifier truyền vào
    )
}