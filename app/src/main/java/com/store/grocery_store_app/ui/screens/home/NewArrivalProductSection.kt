package com.store.grocery_store_app.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.screens.home.components.ProductSection
import com.store.grocery_store_app.ui.theme.ProductGreen
@Composable
fun NewArrivalProducts(
    viewModel: NewArrivalsViewModel,
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel(),
    onSeeMoreClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    onAddToCartClick: (ProductResponse) -> Unit
) {
    val state by viewModel.state.collectAsState()
    ProductSection(
        title = "Hàng Mới Về",
        icon = Icons.Default.FiberNew, // Icon "NEW" thay vì NewReleases
        iconTint = Color(0xFF4CAF50), // Giữ màu xanh cho icon
        headerColor = Color.Black, // Màu đen cho chữ "Hàng Mới Về"
        products = state.newArrivals,
        isLoading = state.isLoading,
        error = state.error,
        favouriteViewModel = favouriteViewModel,
        onSeeMoreClick = onSeeMoreClick,
        onProductClick = onProductClick,
        onAddToCartClick = onAddToCartClick,
    )
}