package com.store.grocery_store_app.ui.screens.ProductsByCategory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.screens.home.ProductViewModel
import com.store.grocery_store_app.ui.screens.home.components.ProductCard
import com.store.grocery_store_app.ui.theme.BackgroundWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsByCategoryScreen(
    categoryId: Long,
    viewModel: ProductViewModel = hiltViewModel(),
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToProductDetails: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val gridState = rememberLazyGridState()

    // Thiết lập danh mục khi màn hình được hiển thị
    LaunchedEffect(categoryId) {
        viewModel.setCategoryAndLoadName(categoryId)
    }

    // Xử lý tải thêm khi cuộn tới cuối
    LaunchedEffect(gridState) {
        if (state.productsByCategory.isNotEmpty() &&
            !state.categoryProductsLoading &&
            state.hasMoreCategoryProducts) {

            val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = state.productsByCategory.size

            if (lastVisibleItemIndex >= totalItems - 4) {
                viewModel.loadNextCategoryPage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.categoryName.ifEmpty { "Sản phẩm theo danh mục" }) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            if (state.categoryProductsLoading && state.productsByCategory.isEmpty()) {
                // Hiển thị loading khi đang tải và chưa có sản phẩm
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (state.categoryProductsError != null && state.productsByCategory.isEmpty()) {
                // Hiển thị thông báo lỗi
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Đã xảy ra lỗi: ${state.categoryProductsError}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refreshCategoryProducts() }) {
                        Text("Thử lại")
                    }
                }
            } else if (state.productsByCategory.isEmpty()) {
                // Hiển thị thông báo khi không có sản phẩm
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có sản phẩm nào trong danh mục này",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Hiển thị danh sách sản phẩm theo dạng lưới
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = gridState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.productsByCategory) { product ->
                        ProductCard(
                            product = product,
                            favouriteViewModel = favouriteViewModel,
                            onProductClick = { onNavigateToProductDetails(product.id) },
                            onAddToCartClick = { /* Xử lý thêm vào giỏ hàng */ }
                        )
                    }

                    // Hiển thị loading ở cuối danh sách khi đang tải thêm
                    if (state.categoryProductsLoading && state.productsByCategory.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}