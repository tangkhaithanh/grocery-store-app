package com.store.grocery_store_app.ui.screens.home

import CategoriesSection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.screens.auth.AuthViewModel
import com.store.grocery_store_app.ui.screens.home.components.BottomNavigation
import com.store.grocery_store_app.ui.screens.home.components.HeaderSection
import com.store.grocery_store_app.ui.screens.home.components.ProfileMenu
import com.store.grocery_store_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(), // Thêm ProductViewModel
    onNavigateToOrder: () -> Unit,

    ) {
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()
    val categoryState by categoryViewModel.state.collectAsState()

    // Check if the user is logged in
    val isUserLoggedIn = authState.isLoggedIn

    var showProfileMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                { onNavigateToOrder() },
                notificationCount = 10
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
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header with search and location
                HeaderSection(
                    isUserLoggedIn = isUserLoggedIn,
                    onProfileClick = { showProfileMenu = true }
                )

                // Categories
                CategoriesSection(
                    viewModel = categoryViewModel,
                    onCategoryClick = { category ->
                        // Handle category selection
                        categoryViewModel.selectCategory(category.id)
                        // Additional actions when a category is clicked
                    }
                )

                // Recommendation section
                BestSellerProducts(
                    viewModel = productViewModel,
                    onSeeMoreClick = {
                        // Xử lý khi người dùng nhấn "See more"
                        // Ví dụ: Chuyển đến trang danh sách sản phẩm bán chạy
                    },
                    onProductClick = { productId ->
                        // Xử lý khi người dùng nhấn vào sản phẩm
                        // Ví dụ: Chuyển đến trang chi tiết sản phẩm
                    },
                    onAddToCartClick = { product ->
                        // Xử lý khi người dùng thêm sản phẩm vào giỏ hàng
                        // Ví dụ: Gọi hàm thêm vào giỏ hàng trong ViewModel
                    }
                )

                // Featured section header (just the title)
                FeaturedSectionHeader()

                // Bottom space for scrolling
                Spacer(modifier = Modifier.height(50.dp))
            }

            // Profile menu dropdown
            if (showProfileMenu) {
                ProfileMenu(
                    isLoggedIn = isUserLoggedIn,
                    userName = authState.userName ?: "Người dùng",
                    userEmail = authState.userEmail ?: "",
                    onDismiss = { showProfileMenu = false },
                    onLogoutClick = {
                        authViewModel.logout()
                        showProfileMenu = false
                        onLogout() // Sử dụng onLogout callback từ NavGraph
                    }
                )
            }
        }
    }
}


@Composable
fun FeaturedSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Featured",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = DeepTeal
            )
        )

        Text(
            text = "See all",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFE57373)
        )
    }
}