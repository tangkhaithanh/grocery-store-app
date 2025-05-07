package com.store.grocery_store_app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.screens.home.components.CartButton
import com.store.grocery_store_app.ui.screens.home.components.ProductCard
import com.store.grocery_store_app.ui.theme.BackgroundWhite
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.Gray500

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onProductClick: (Long) -> Unit,
    onCartClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val gridState = rememberLazyGridState()

    // Monitor search query changes
    LaunchedEffect(searchQuery.value) {
        viewModel.updateQuery(searchQuery.value)
    }

    // Observe for end of list to load more items
    val endOfListReached by remember {
        derivedStateOf {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ==
                    gridState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(endOfListReached) {
        if (endOfListReached && state.hasMoreItems && !state.isLoading && state.hasSearched) {
            viewModel.loadMoreProducts()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm kiếm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            // Thanh tìm kiếm tùy chỉnh với nút giỏ hàng - tăng kích thước tổng thể
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp), // tăng padding dọc
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thanh tìm kiếm custom - tăng kích thước
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp), // tăng chiều cao lên 48dp để hiển thị đẩy đủ text
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp), // tăng padding ngang
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Gray500,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp)) // thêm khoảng cách

                        // Sử dụng TextField đơn giản với đủ không gian cho text
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = searchQuery.value,
                                onValueChange = { searchQuery.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (searchQuery.value.isNotBlank()) {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            viewModel.search(searchQuery.value)
                                        }
                                    }
                                ),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.value.isEmpty()) {
                                        Text(
                                            text = "Tìm kiếm sản phẩm...",
                                            color = Gray500
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }

                        // Clear button - nếu có text
                        if (searchQuery.value.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery.value = ""
                                    viewModel.clearResults()
                                },
                                modifier = Modifier.size(32.dp) // tăng kích thước button
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Gray500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Tăng khoảng cách giữa thanh tìm kiếm và nút giỏ hàng
                Spacer(modifier = Modifier.width(12.dp))

                // Dùng Container cho nút giỏ hàng để đảm bảo kích thước đủ lớn
                Box(
                    modifier = Modifier
                        .size(48.dp) // đảm bảo kích thước đủ lớn
                        .padding(2.dp)
                ) {
                    CartButton(
                        itemCount = 5,          // hoặc state thực tế
                        onClick   = onCartClick,
                        iconTint  = Color.Black // ← icon hiển thị rõ trên nền sáng
                    )
                }
            }

            // Content area - shows either suggestions or search results
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                // Initially show suggestions
                if (!state.hasSearched) {
                    SuggestionsContent { suggestion ->
                        searchQuery.value = suggestion
                        viewModel.search(suggestion)
                    }
                }

                // Show loading indicator
                if (state.isLoading && state.products.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }

                // Show error message
                if (state.error != null && state.products.isEmpty() && state.hasSearched) {
                    Text(
                        text = state.error ?: "Đã xảy ra lỗi khi tìm kiếm",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Center)
                    )
                }

                // Show empty state
                if (state.isEmpty && !state.isLoading && state.hasSearched) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Không tìm thấy sản phẩm nào",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hãy thử tìm kiếm với từ khóa khác",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Gray500
                        )
                    }
                }

                // Show search results in a grid layout
                if (state.products.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 2 cột để hiển thị đẹp hơn
                        state = gridState,
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.products) { product ->
                            ProductCard(
                                product = product,
                                favouriteViewModel = favouriteViewModel,
                                onProductClick = { onProductClick(product.id) },
                                onAddToCartClick = { /* Handle add to cart */ }
                            )
                        }

                        // Loading indicator at the bottom when loading more items
                        if (state.isLoading && state.products.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionsContent(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Gợi ý tìm kiếm",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // List of suggested search terms
        SuggestedSearchTerm("Rau củ quả", onSuggestionClick)
        SuggestedSearchTerm("Trái cây", onSuggestionClick)
        SuggestedSearchTerm("Sữa tươi", onSuggestionClick)
        SuggestedSearchTerm("Thịt tươi", onSuggestionClick)
        SuggestedSearchTerm("Bánh mì", onSuggestionClick)
        SuggestedSearchTerm("Nước giải khát", onSuggestionClick)
    }
}

@Composable
fun SuggestedSearchTerm(term: String, onClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onClick(term) },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Gray500,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = term,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}