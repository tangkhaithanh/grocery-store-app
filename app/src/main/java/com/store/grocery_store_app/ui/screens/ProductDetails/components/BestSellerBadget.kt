package com.store.grocery_store_app.ui.screens.ProductDetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Reusable product badge component that displays an icon and text
 * on a colored surface, typically used to highlight product features.
 *
 * @param text The text to display in the badge
 * @param icon The icon to display next to the text
 * @param textColor Color for both the text and icon
 * @param backgroundColor Background color of the badge
 * @param modifier Optional modifier for the parent Row
 */
@Composable
fun ProductBadge(
    text: String,
    icon: ImageVector,
    textColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}

/**
 * Specific implementation of ProductBadge for best-selling products.
 *
 * @param soldCount The number of items sold
 * @param threshold The minimum number of sales to qualify as a best seller
 * @param modifier Optional modifier for the parent Row
 */
@Composable
fun BestSellerBadge(
    soldCount: Int,
    threshold: Int = 100,
    textColor: Color = Color(0xFFFF5252), // ErrorLight
    modifier: Modifier = Modifier
) {
    if (soldCount > threshold) {
        ProductBadge(
            text = "Sản phẩm bán chạy",
            icon = androidx.compose.material.icons.Icons.Default.LocalFireDepartment,
            textColor = textColor,
            backgroundColor = textColor.copy(alpha = 0.1f),
            modifier = modifier
        )
    }
}