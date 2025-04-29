package com.store.grocery_store_app.ui.screens.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.IconBread
import com.store.grocery_store_app.ui.theme.IconCleaner
import com.store.grocery_store_app.ui.theme.IconDairy
import com.store.grocery_store_app.ui.theme.IconSweets
import com.store.grocery_store_app.ui.theme.ProductGreen
import com.store.grocery_store_app.ui.theme.ProductRed
import java.util.Locale

@Composable
fun ProductCard(
    product: ProductResponse,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit = {}
) {
    // Determine the category color based on the product category
    val categoryColor = getCategoryColor(product.categoryName)

    // Calculate discount percentage if there is a discount
    val discountPercentage = if (product.effectivePrice < product.price) {
        ((1 - (product.effectivePrice.toDouble() / product.price.toDouble())) * 100).toInt()
    } else null

    // Determine if product should have "Bán chạy" badge
    val bestSellerBadge = if (product.soldCount > 100) "Bán chạy" else null

    // Use our reusable product card component
    CustomProductCard(
        image = product.imageUrls.firstOrNull(),
        name = product.name,
        category = product.categoryName,
        categoryColor = categoryColor,
        price = product.price.toDouble(),
        effectivePrice = product.effectivePrice.toDouble(),
        rating = product.averageRating,
        soldCount = product.soldCount,
        quantity = product.quantity,
        badgeText = bestSellerBadge,
        badgeBackgroundColor = Color(0xFFFF5252),
        discountPercentage = discountPercentage,
        addToCartButtonColor = DeepTeal,
        priceColor = DeepTeal,
        currencyLocale = Locale("vi", "VN"),
        onProductClick = onProductClick,
        onAddToCartClick = onAddToCartClick
    )
}

/**
 * Helper function to get the appropriate color based on product category.
 */
private fun getCategoryColor(categoryName: String): Color {
    return when {
        categoryName.contains("fruit", ignoreCase = true) -> ProductGreen
        categoryName.contains("vegetable", ignoreCase = true) -> ProductGreen
        categoryName.contains("dairy", ignoreCase = true) -> IconDairy
        categoryName.contains("bakery", ignoreCase = true) -> IconBread
        categoryName.contains("sweets", ignoreCase = true) -> IconSweets
        categoryName.contains("clean", ignoreCase = true) -> IconCleaner
        else -> ProductRed
    }
}