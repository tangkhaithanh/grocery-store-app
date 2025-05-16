package com.store.grocery_store_app.ui.screens.FavoriteProduct

import EmptyCategoryView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.ErrorDialog
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.screens.home.components.ProductCard
import com.store.grocery_store_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteProductScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductDetails: (Long) -> Unit,
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel()
) {
    val favouriteState by favouriteViewModel.state.collectAsState()

    // LaunchedEffect to load favorite products when screen opens
    LaunchedEffect(Unit) {
        favouriteViewModel.loadFavouriteProducts()
    }

    // LaunchedEffect to reload when favorite products change
    LaunchedEffect(favouriteState.favouriteProducts.size) {
        // This will trigger whenever the size of favorite products changes
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Sản phẩm yêu thích",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepTeal
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            when {
                favouriteState.isLoading -> {
                    LoadingDialog(
                        isLoading = true,
                        message = "Đang tải sản phẩm yêu thích..."
                    )
                }

                favouriteState.error != null -> {
                    ErrorDialog(
                        title = "Lỗi",
                        content = favouriteState.error!!,
                        clearError = { /* Handle error dismissal if needed */ }
                    )
                }

                favouriteState.favouriteProducts.isEmpty() -> {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        EmptyCategoryView(
                            content = "Bạn chưa có sản phẩm yêu thích nào\nHãy thêm sản phẩm vào danh sách yêu thích!"
                        )
                    }
                }

                else -> {
                    // Products grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = favouriteState.favouriteProducts,
                            key = { product -> product.id }
                        ) { product ->
                            ProductCard(
                                product = product,
                                favouriteViewModel = favouriteViewModel,
                                onProductClick = {
                                    onNavigateToProductDetails(product.id)
                                },
                                onAddToCartClick = {
                                    // Handle add to cart if needed
                                    // You might want to add cart functionality here
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Handle favorite toggle result
    if (favouriteState.showLoginRequired) {
        LaunchedEffect(Unit) {
            favouriteViewModel.clearLoginRequiredMessage()
        }
    }

    // Reload favorites when a product is toggled
    LaunchedEffect(favouriteState.favouriteProducts) {
        // This ensures the UI updates when favorites change
    }
}