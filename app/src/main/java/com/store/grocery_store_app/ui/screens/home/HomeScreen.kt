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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.screens.FlashSale.FlashSaleSection
import com.store.grocery_store_app.ui.screens.auth.AuthViewModel
import com.store.grocery_store_app.ui.screens.cart.CartViewModel
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
    productViewModel: ProductViewModel = hiltViewModel(),
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel(),
    newArrivalsViewModel: NewArrivalsViewModel = hiltViewModel(),
    cartViewModel : CartViewModel = hiltViewModel(),
    onNavigateToOrder: () -> Unit,
    onNavigateToProductDetails: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    onNavigateToCategory: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToAccount: () -> Unit = {}
    ) {
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()
    val categoryState by categoryViewModel.state.collectAsState()
    val isUserLoggedIn = authState.isLoggedIn
    var showProfileMenu by remember { mutableStateOf(false) }
    val cartState by cartViewModel.state.collectAsState()
    val cartItems = cartState.cartItems
    val state by productViewModel.state.collectAsState()
    val flashSales = state.flashSales
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            cartViewModel.getAllCartItem()
            favouriteViewModel.loadFavouriteProducts()
            productViewModel.loadFlashSale()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                onFabClick = { onNavigateToOrder() },
                notificationCount = 10,
                onNavigateToHome = { /* Đã ở trang Home nên không cần xử lý */ },
                onNavigateToCategory = onNavigateToCategory,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToAccount = onNavigateToAccount,
                selectedTab = 0  // Tab Home (index = 0)
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
                    onProfileClick = { showProfileMenu = true },
                    onCartClick = onNavigateToCart,             // Dùng callback từ HomeScreen
                    onNavigateToSearch = onNavigateToSearch,    // Dùng cùng callback với onSearchClick
                    cartItemCount = cartItems.count(),                         // Có thể thay bằng số lượng thực tế từ giỏ hàng
                    locationName = "TP Hồ Chí Minh, VN",        // Có thể thay bằng vị trí thực tế của người dùng
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
                if(flashSales.isEmpty()) {
                    FlashSaleSection(
                        title = "FlashSale", // Hoặc "Sắp diễn ra", "Không có Flash Sale"
                        listProduct = emptyList(),       // Danh sách sản phẩm rỗng
                        isLoading = false,               // Không loading vì chúng ta biết là không có
                        error = null,                    // Không có lỗi cụ thể ở đây
                        favouriteViewModel = favouriteViewModel, // Vẫn cần truyền nếu ProductCard yêu cầu
                        onSeeMoreClick = {
                            // Có thể không làm gì, hoặc điều hướng đến một trang thông tin chung về FlashSale
                        },
                        onProductClick = { /* Sẽ không bao giờ được gọi vì listProduct rỗng */ },
                        onAddToCartClick = { /* Sẽ không bao giờ được gọi */ }
                        // Bạn có thể muốn tùy chỉnh icon hoặc màu sắc cho trường hợp "chưa diễn ra" này
                        // icon = Icons.Default.HourglassEmpty, // Ví dụ
                        // iconTint = Color.Gray,
                        // headerColor = Color.DarkGray
                    )
                }
                else {
                    flashSales.forEach { flashSale ->
                        val productList: List<ProductResponse> = flashSale.flashSaleItems.map { flashItem ->
                            flashItem.product
                        }
                        FlashSaleSection(
                            title = flashSale.name,
                            listProduct = productList,
                            onSeeMoreClick = {
                                // Navigate to a full list of best sellers (could be implemented later)
                            },
                            onProductClick = { productId ->
                                // Navigate to product details when a product is clicked
                                onNavigateToProductDetails(productId)
                            },
                            onAddToCartClick = { product ->
                                // Handle add to cart (could be implemented with CartViewModel)
                            }
                        )
                    }
                }

                BestSellerProducts(
                    viewModel = productViewModel,
                    favouriteViewModel = favouriteViewModel,
                    onSeeMoreClick = {
                        // Navigate to a full list of best sellers (could be implemented later)
                    },
                    onProductClick = { productId ->
                        // Navigate to product details when a product is clicked
                        onNavigateToProductDetails(productId)
                    },
                    onAddToCartClick = { product ->
                        // Handle add to cart (could be implemented with CartViewModel)
                    }
                )

                // New Arrivals section
                NewArrivalProducts(
                    viewModel = newArrivalsViewModel,
                    favouriteViewModel = favouriteViewModel,
                    onSeeMoreClick = {
                        // Navigate to a full list of new arrivals (could be implemented later)
                    },
                    onProductClick = { productId ->
                        // Navigate to product details when a product is clicked
                        onNavigateToProductDetails(productId)
                    },
                    onAddToCartClick = { product ->
                        // Handle add to cart (could be implemented with CartViewModel)
                    }
                )


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