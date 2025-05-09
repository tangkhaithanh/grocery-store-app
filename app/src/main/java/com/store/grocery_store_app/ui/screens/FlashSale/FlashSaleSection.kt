package com.store.grocery_store_app.ui.screens.FlashSale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.screens.home.ProductViewModel
import com.store.grocery_store_app.ui.screens.home.components.ProductSection

@Composable
fun FlashSaleSection(
    viewModel: ProductViewModel,
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel(),
    onSeeMoreClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    onAddToCartClick: (ProductResponse) -> Unit
) {
    val state by viewModel.state.collectAsState()

    ProductSection(
        title = "Bán chạy",
        icon = Icons.Default.Star,
        iconTint = Color(0xFFFFB74D),
        headerColor = Color(0xFF212121),
        products = state.bestSellerProducts,
        isLoading = state.isLoading,
        error = state.error,
        favouriteViewModel = favouriteViewModel, // Truyền xuống ProductSection
        onSeeMoreClick = onSeeMoreClick,
        onProductClick = onProductClick,
        onAddToCartClick = onAddToCartClick,
    )
}