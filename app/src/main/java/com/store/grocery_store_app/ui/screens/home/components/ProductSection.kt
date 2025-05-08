package com.store.grocery_store_app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.theme.DeepTeal

/**
 * Reusable product section component that can be used to display various product categories
 *
 * @param title Section title displayed in the header
 * @param icon Icon displayed in the header (defaults to Star)
 * @param iconTint Color for the icon (defaults to DeepTeal)
 * @param headerColor Color for the header text (defaults to DeepTeal)
 * @param products List of products to display
 * @param isLoading Loading state indicator
 * @param error Error message if loading failed
 * @param onSeeMoreClick Callback when "See more" button is clicked
 * @param onProductClick Callback when a product is clicked
 * @param onAddToCartClick Callback when add to cart button is clicked
 */
@Composable
fun ProductSection(
    title: String,
    icon: ImageVector = Icons.Default.Star,
    iconTint: Color = DeepTeal,
    headerColor: Color = DeepTeal,
    products: List<ProductResponse>,
    isLoading: Boolean = false,
    error: String? = null,
    favouriteViewModel: FavoriteProductViewModel,
    onSeeMoreClick: () -> Unit = {},
    onProductClick: (Long) -> Unit = {},
    onAddToCartClick: (ProductResponse) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon for the section
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = headerColor
                    )
                )
            }

            Button(
                onClick = onSeeMoreClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFE57373)
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "See more",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Loading indicator
        if (isLoading && products.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                CircularProgressIndicator(color = iconTint)
            }
        }

        // Error message
        if (error != null && products.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Text(
                    text = "Không thể tải sản phẩm: $error",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
            }
        }

        // Products row
        if (products.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        favouriteViewModel = favouriteViewModel, // Truyền favouriteViewModel
                        onProductClick = { onProductClick(product.id) },
                        onAddToCartClick = { onAddToCartClick(product) }
                    )
                }
            }
        }
    }
}

