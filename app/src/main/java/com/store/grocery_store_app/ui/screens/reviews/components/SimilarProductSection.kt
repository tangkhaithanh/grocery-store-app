package com.store.grocery_store_app.ui.screens.reviews.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.home.components.ProductCard
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.Gray600

/**
 * Similar products section that displays products in a grid layout
 *
 * @param currentProductId ID of the current product to exclude from the list
 * @param products List of similar products to display
 * @param isLoading Loading state indicator
 * @param error Error message if loading failed
 * @param onProductClick Callback when a product is clicked
 * @param onAddToCartClick Callback when add to cart button is clicked
 * @param onSeeMoreClick Callback when "See more" button is clicked
 */
@Composable
fun SimilarProductsSection(
    currentProductId: Long,
    products: List<ProductResponse>,
    isLoading: Boolean = false,
    error: String? = null,
    onProductClick: (Long) -> Unit,
    onAddToCartClick: (ProductResponse) -> Unit = {},
    onSeeMoreClick: () -> Unit = {}
) {
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
            // Header with title and "See more" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sản phẩm tương tự",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Xem thêm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeepTeal,
                    modifier = Modifier.clickable { onSeeMoreClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on state
            when {
                isLoading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        CircularProgressIndicator(color = DeepTeal)
                    }
                }
                error != null -> {
                    Text(
                        text = "Không thể tải sản phẩm tương tự: $error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                products.isEmpty() -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Text(
                            text = "Chưa có sản phẩm tương tự",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray600,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    // Filter out current product
                    val filteredProducts = products.filter { it.id != currentProductId }

                    if (filteredProducts.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            Text(
                                text = "Chưa có sản phẩm tương tự",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray600,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Grid of products
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.height(420.dp) // Fixed height to show approximately 2 rows
                        ) {
                            items(filteredProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = { onProductClick(product.id) },
                                    onAddToCartClick = { onAddToCartClick(product) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}