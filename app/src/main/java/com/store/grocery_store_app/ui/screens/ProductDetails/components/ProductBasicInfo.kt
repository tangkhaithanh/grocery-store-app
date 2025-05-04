package com.store.grocery_store_app.ui.screens.ProductDetails.components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.Gray600
/**
 * Basic product information section
 */
@Composable
fun ProductBasicInfo(
    categoryName: String,
    productName: String,
    rating: Double?,
    soldCount: Int
) {
    // Category
    Text(
        text = categoryName,
        style = MaterialTheme.typography.labelLarge,
        color = DeepTeal
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Product Name
    Text(
        text = productName,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Ratings
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", rating ?: 0.0),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Đã bán $soldCount",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray600
        )
    }
}
