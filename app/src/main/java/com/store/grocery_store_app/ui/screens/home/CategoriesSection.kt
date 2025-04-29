import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.store.grocery_store_app.data.models.response.CategoryResponse
import com.store.grocery_store_app.ui.screens.home.CategoryViewModel

// Các màu sắc chủ đạo
val DeepTeal = Color(0xFF006064)
val BackgroundWhite = Color(0xFFF5F5F5)
val AccentOrange = Color(0xFFFF8A65)
val LightTeal = Color(0xFFB2DFDB)
val DarkGray = Color(0xFF424242)
val ErrorRed = Color(0xFFD32F2F)

// Màu nền cho các loại category
val BreadColor = Color(0xFFFFF3E0)
val CleanerColor = Color(0xFFE1F5FE)
val SweetsColor = Color(0xFFF8BBD0)
val DairyColor = Color(0xFFE0F7FA)
val FruitsColor = Color(0xFFE8F5E9)
val VegetablesColor = Color(0xFFDCEDC8)
val MeatColor = Color(0xFFFFEBEE)
val BeverageColor = Color(0xFFEDE7F6)

@Composable
fun CategoriesSection(
    viewModel: CategoryViewModel = hiltViewModel(),
    onCategoryClick: (CategoryResponse) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val categories = state.categories
    val isLoading = state.isLoading
    val error = state.error

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .padding(bottom = 16.dp) // Separated the padding modifiers
            .background(BackgroundWhite)
    ) {
        // Header row with title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp), // Separated padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepTeal
                )
            )

            // You can optionally add "See all" here if needed
            // Text(
            //     text = "Xem tất cả",
            //     style = MaterialTheme.typography.bodyMedium,
            //     color = AccentOrange
            // )
        }

        // Categories content
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when {
                isLoading -> {
                    CategoryLoadingIndicator()
                }
                error != null -> {
                    CategoryErrorView(error = error) {
                        viewModel.loadCategories()
                    }
                }
                categories.isNotEmpty() -> {
                    // Categories row with adjusted spacing
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                            .padding(end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                isSelected = category.id == state.selectedCategoryId,
                                onClick = { onCategoryClick(category) }
                            )
                        }
                    }
                }
                else -> {
                    EmptyCategoryView()
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CategoryItem(
    category: CategoryResponse,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Map category name to a background color (simplified version)
    val bgColor = when {
        category.name.contains("Bread", ignoreCase = true) -> BreadColor
        category.name.contains("Clean", ignoreCase = true) -> CleanerColor
        category.name.contains("Sweet", ignoreCase = true) -> SweetsColor
        category.name.contains("Dairy", ignoreCase = true) -> DairyColor
        category.name.contains("Fruit", ignoreCase = true) -> FruitsColor
        category.name.contains("Vegetable", ignoreCase = true) -> VegetablesColor
        category.name.contains("Meat", ignoreCase = true) -> MeatColor
        category.name.contains("Beverage", ignoreCase = true) -> BeverageColor
        else -> LightTeal
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(85.dp)  // Set fixed width for consistency
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        // Image container with shadow and background
        Box(
            modifier = Modifier
                .size(85.dp)  // Consistent size
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape,
                    spotColor = Color.Gray.copy(alpha = 0.3f)
                )
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            // Load image using Glide to fill the entire circle
            GlideImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier
                    .size(85.dp)  // Match the container size exactly
                    .clip(CircleShape),
                contentScale = ContentScale.Crop  // Crop to ensure image fills the circle
            ) {
                it.placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()  // Apply centerCrop in Glide request
            }
        }

        // Label with fixed height for consistency
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) DeepTeal else DarkGray
            ),
            modifier = Modifier
                .padding(top = 8.dp)
                .height(20.dp),  // Fixed height for text
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        // Selected indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(24.dp)
                    .height(3.dp)  // Slightly thicker indicator
                    .background(
                        color = AccentOrange,
                        shape = RoundedCornerShape(1.5.dp)
                    )
            )
        } else {
            // Empty box to maintain consistent height
            Spacer(modifier = Modifier.height(7.dp))
        }
    }
}

@Composable
fun CategoryLoadingIndicator() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(5) {
            CategorySkeletonItem()
        }
    }
}

@Composable
fun CategorySkeletonItem() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = alpha))
        )

        // Text placeholder
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray.copy(alpha = alpha))
        )
    }
}

@Composable
fun CategoryErrorView(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = ErrorRed,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
        ) {
            Text("Thử lại")
        }
    }
}

@Composable
fun EmptyCategoryView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Không tìm thấy danh mục nào",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}