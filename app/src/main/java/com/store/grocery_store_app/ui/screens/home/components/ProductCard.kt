package com.store.grocery_store_app.ui.screens.home.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
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
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel(),
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

    // Thu thập trạng thái yêu thích từ ViewModel
    val isFavourite by favouriteViewModel.isFavourite(product.id)
        .collectAsState(initial = false)

    // Thu thập trạng thái chung từ ViewModel
    val favouriteState by favouriteViewModel.state.collectAsState()

    // Đặt LaunchedEffect ở cấp Composable, không phải trong câu lệnh if
    if (favouriteState.showLoginRequired) {
        val context = LocalContext.current
        LaunchedEffect(true) {
            Toast.makeText(
                context,
                "Vui lòng đăng nhập để thêm sản phẩm vào yêu thích",
                Toast.LENGTH_SHORT
            ).show()
            favouriteViewModel.clearLoginRequiredMessage()
        }
    }

    favouriteState.error?.let { error ->
        val context = LocalContext.current
        LaunchedEffect(error) {
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
        isFavourite = isFavourite,
        onFavouriteClick = {
            favouriteViewModel.toggleFavourite(product.id)
        },
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