package com.store.grocery_store_app.ui.screens.home
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.home.components.ProductSection
import com.store.grocery_store_app.ui.theme.*
@Composable
fun BestSellerProducts(
    viewModel: ProductViewModel = hiltViewModel(),
    onSeeMoreClick: () -> Unit = {},
    onProductClick: (Long) -> Unit = {},
    onAddToCartClick: (ProductResponse) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    ProductSection(
        title = "Best Seller",
        icon = Icons.Default.Star,
        iconTint = DeepTeal,
        headerColor = DeepTeal,
        products = state.bestSellerProducts,
        isLoading = state.isLoading,
        error = state.error,
        onSeeMoreClick = onSeeMoreClick,
        onProductClick = onProductClick,
        onAddToCartClick = onAddToCartClick
    )
}