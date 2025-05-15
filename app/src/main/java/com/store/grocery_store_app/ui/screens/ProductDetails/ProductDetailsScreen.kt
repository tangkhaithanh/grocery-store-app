package com.store.grocery_store_app.ui.screens.ProductDetails
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.Animation.AddToCartAnimation
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.ui.screens.ProductDetails.components.AddToCartButton
import com.store.grocery_store_app.ui.screens.ProductDetails.components.AddToCartSheet
import com.store.grocery_store_app.ui.screens.ProductDetails.components.BestSellerBadge
import com.store.grocery_store_app.ui.screens.ProductDetails.components.ErrorContent
import com.store.grocery_store_app.ui.screens.ProductDetails.components.ImageThumbnails
import com.store.grocery_store_app.ui.screens.ProductDetails.components.PriceSection
import com.store.grocery_store_app.ui.screens.ProductDetails.components.ProductBasicInfo
import com.store.grocery_store_app.ui.screens.ProductDetails.components.ProductDescriptionCard
import com.store.grocery_store_app.ui.screens.ProductDetails.components.ProductDetailsTopBar
import com.store.grocery_store_app.ui.screens.ProductDetails.components.ProductImageCarousel
import com.store.grocery_store_app.ui.screens.ProductDetails.components.QuantityInfo
import com.store.grocery_store_app.ui.screens.cart.CartViewModel
import com.store.grocery_store_app.ui.screens.reviews.ReviewViewModel
import com.store.grocery_store_app.ui.screens.reviews.components.ReviewSection
import com.store.grocery_store_app.ui.screens.reviews.components.SimilarProductsSection
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.ErrorLight
import com.store.grocery_store_app.ui.theme.Gray600
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Long,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAddToCartSuccess: () -> Unit,
    onNavigateToProduct: (Long) -> Unit, // Thêm callback này để điều hướng đến sản phẩm khác
    onNavigateToCart: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val reviewState by reviewViewModel.state.collectAsState()
    val similarProductsState by viewModel.similarProductsState.collectAsState()
    val scrollState = rememberScrollState()

    // Biến trạng thái cho animation
    var isAnimating by remember { mutableStateOf(false) }
    var cartButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var addToCartButtonPosition by remember { mutableStateOf(Offset.Zero) }
    var showSheet by remember { mutableStateOf(false) }
    //snackbar
    val cartState by cartViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // Dùng scope này cho LaunchedEffect

    // Currency formatter for Vietnamese Dong
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    LaunchedEffect(cartState.isSuccessInsert) {
        if (cartState.isSuccessInsert) {
            scope.launch { // Sử dụng scope đã nhớ
                val result = snackbarHostState.showSnackbar(
                    message = "Sản phẩm đã được thêm vào giỏ hàng",
                    actionLabel = "Xem giỏ hàng",
                    duration = SnackbarDuration.Short,
                    withDismissAction = true
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onNavigateToCart() // Gọi callback điều hướng
                }
            }
            cartViewModel.clearSuccessFlag() // Gọi hàm để reset cờ isSuccess trong ViewModel
            // Sau khi thêm thành công, bạn có thể muốn tự động đóng BottomSheet nếu nó vẫn mở
            if (showSheet) {
                showSheet = false // Đóng sheet
            }
        }
    }
    // Load product details
    LaunchedEffect(productId) {
        viewModel.loadProductDetails(productId)
        reviewViewModel.loadProductReviews(productId, true)
        reviewViewModel.loadReviewStats(productId)  // Gọi API để lấy thống kê đánh giá
    }
    // Function to handle animation completion
    fun onAnimationComplete() {
        // Reset animation state
        isAnimating = false

        // Call the actual add to cart functionality
        //onAddToCartSuccess()
    }

    // Add a new LaunchedEffect that triggers when the product loads
    LaunchedEffect(state.product) {
        if (state.product != null) {
            viewModel.loadSimilarProducts()
        }
    }
    Scaffold(
        topBar = {
            ProductDetailsTopBar(
                isFavorite = state.isFavorite,
                onNavigateBack = onNavigateBack,
                onToggleFavorite = { viewModel.toggleFavorite() },
                onCartClick = onNavigateToCart, // Thêm xử lý sự kiện nhấp vào giỏ hàng
                cartItemCount = 5, // Có thể thay bằng số lượng thực tế từ CartViewModel
                onCartPositioned = { position ->
                    cartButtonPosition = position
                }
            )
        },
        bottomBar = {
            state.product?.let { product ->
                val remaining = product.quantity - product.soldCount

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AddToCartButton(
                            remaining = remaining,
                            onShowSheet = { showSheet = true },
                            onAddToCart = {
                                if (!isAnimating &&
                                    cartButtonPosition != Offset.Zero &&
                                    addToCartButtonPosition != Offset.Zero
                                ) {
                                    isAnimating = true
                                }
                            },
                            // ⬇⬇ Lấy đúng tọa độ tâm nút
                            modifier = Modifier
                                .onGloballyPositioned { c ->
                                    val cx = c.positionInRoot().x + c.size.width / 2
                                    val cy = c.positionInRoot().y + c.size.height / 2
                                    addToCartButtonPosition = Offset(cx, cy)
                                }
                                .fillMaxWidth()
                                // hoặc kích thước bạn muốn
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                // Tùy chỉnh Snackbar ở đây nếu muốn
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = if (snackbarData.visuals.actionLabel == "Đóng") Color.Red else Color(0xFF4CAF50),
                    contentColor = Color.White,
                    actionColor = MaterialTheme.colorScheme.primary // Ví dụ
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if(showSheet) {
                Log.d("SHOW SHEET", "TEST")
                AddToCartSheet(
                    product = state.product!!,
                    onDismiss = { showSheet = false},
                    onNavigateToCart = onNavigateToCart
                    )
            }
            // Show loading dialog while fetching data
            if (state.isLoading) {
                LoadingDialog(
                    isLoading = true,
                    message = "Đang tải thông tin sản phẩm..."
                )
            }

            // Show error if there's an issue
            state.error?.let { error ->
                ErrorContent(
                    error = error,
                    onNavigateBack = onNavigateBack
                )
            }

            // Show product details once loaded
            state.product?.let { product ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Image section with background and padding
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F8F8))
                            .padding(vertical = 16.dp)
                    ) {
                        Column {
                            // Image carousel
                            ProductImageCarousel(
                                images = product.imageUrls.ifEmpty { listOf(null) },
                                currentImageIndex = state.currentImageIndex,
                                onImageChange = { viewModel.changeCurrentImage(it) },
                                productName = product.name
                            )

                            // Thumbnails
                            ImageThumbnails(
                                images = product.imageUrls.ifEmpty { listOf(null) },
                                currentImageIndex = state.currentImageIndex,
                                onThumbnailClick = { viewModel.changeCurrentImage(it) }
                            )
                        }
                    }

                    // Product info card with elevation
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            BestSellerBadge(soldCount = product.soldCount)
                            // Basic product info
                            ProductBasicInfo(
                                categoryName = product.categoryName,
                                productName = product.name,
                                rating = product.averageRating,
                                soldCount = product.soldCount
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Divider with custom style
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = Color(0xFFEEEEEE),
                                thickness = 1.dp
                            )

                            // Price section with styled background
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (product.effectivePrice < product.price)
                                            Color(0xFFFFF8F3) else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    PriceSection(
                                        price = product.price,
                                        effectivePrice = product.effectivePrice,
                                        currencyFormatter = currencyFormatter
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Delivery info section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Quantity with styled presentation
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Inventory2,
                                            contentDescription = null,
                                            tint = Gray600,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        QuantityInfo(
                                            quantity = product.quantity,
                                            soldCount = product.soldCount
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Description card
                    ProductDescriptionCard(description = product.description)

                    Spacer(modifier = Modifier.height(24.dp))
                    // Reviews section - Added component
                    ReviewSection(
                        reviews = reviewState.reviews,
                        stats = reviewState.reviewStats,
                        isLoading = reviewState.isLoading,
                        hasMoreReviews = reviewState.hasMorePages,
                        error = reviewState.error,
                        onLoadMore = { reviewViewModel.loadProductReviews(productId) }
                    )

                    SimilarProductsSection(
                        currentProductId = product.id,
                        products = similarProductsState.products,
                        isLoading = similarProductsState.isLoading,
                        error = similarProductsState.error,
                        onProductClick = { similarProductId ->
                            // Sử dụng callback để điều hướng đến trang chi tiết sản phẩm mới
                            onNavigateToProduct(similarProductId)
                        },
                        onAddToCartClick = { similarProduct ->
                            // Handle adding similar product to cart
                            // Cũng có thể thêm logic thông báo thành công ở đây
                        },
                        onSeeMoreClick = {
                            // Điều hướng đến danh sách sản phẩm theo danh mục
                            // Nếu cần thêm callback để điều hướng đến category
                        }
                    )
                }
            }
            // Hiệu ứng thêm vào giỏ hàng
            if (cartButtonPosition != Offset.Zero && addToCartButtonPosition != Offset.Zero) {
                // Lấy URL hình ảnh đầu tiên từ sản phẩm nếu có
                val imageUrl = state.product?.imageUrls?.firstOrNull()

                AddToCartAnimation(
                    imageUrl = imageUrl,
                    isPlaying = isAnimating,
                    sourcePosition = addToCartButtonPosition,
                    cartPosition = cartButtonPosition,
                    onAnimationEnd = ::onAnimationComplete
                )
            }

        }
    }
}