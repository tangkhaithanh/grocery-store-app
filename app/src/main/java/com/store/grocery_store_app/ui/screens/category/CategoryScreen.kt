package com.store.grocery_store_app.ui.screens.category
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.screens.home.CategoryViewModel
import com.store.grocery_store_app.ui.screens.home.components.BottomNavigation
import com.store.grocery_store_app.ui.theme.BackgroundWhite
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateToProductsByCategory: (Long) -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {}
) {
    val categoryState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh mục sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                onFabClick = { onNavigateToOrder() },
                notificationCount = 10,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCategory = { /* Đã ở trang Category nên không cần xử lý */ },
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToAccount = onNavigateToAccount,
                selectedTab = 1  // Tab Categories (index = 1)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Đã loại bỏ tiêu đề "Danh mục sản phẩm" vì nó đã có trong TopAppBar
                if (categoryState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (categoryState.error != null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Đã xảy ra lỗi: ${categoryState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadCategories() }) {
                            Text("Thử lại")
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(categoryState.categories) { category ->
                            CategoryItem(
                                category = category,
                                onClick = { onNavigateToProductsByCategory(category.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}