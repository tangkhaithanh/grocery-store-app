package com.store.grocery_store_app.ui.screens.reviews.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.data.models.response.ReviewStatsResponse
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.Gray600
import java.util.*
/**
 * Component chính hiển thị phần đánh giá
 */
@Composable
fun ReviewSection(
    reviews: List<ReviewResponse>,
    stats: ReviewStatsResponse?,
    isLoading: Boolean,
    hasMoreReviews: Boolean,
    error: String?,
    onLoadMore: () -> Unit
) {
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

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
            // Tiêu đề phần đánh giá
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đánh giá và nhận xét",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (stats != null) {
                    Text(
                        text = "Tất cả (${stats.totalReviews})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phần tổng quan đánh giá
            if (stats != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Bên trái: Đánh giá trung bình
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = String.format("%.1f", stats.averageRating),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepTeal
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "${stats.totalReviews} đánh giá",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Gray600
                                )
                            }
                        }

                        // Bên phải: Phân phối đánh giá
                        Column(
                            modifier = Modifier.width(200.dp)
                        ) {
                            for (i in 5 downTo 1) {
                                val ratingCount = stats.ratingDistribution["$i"]?.toInt() ?: 0
                                val percentage = if (stats.totalReviews > 0) {
                                    (ratingCount.toFloat() / stats.totalReviews) * 100f
                                } else 0f

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$i",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.width(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color.LightGray.copy(alpha = 0.5f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(percentage / 100f)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(DeepTeal)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = "$ratingCount",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Gray600,
                                        modifier = Modifier.width(24.dp),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trạng thái loading
            if (isLoading && reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DeepTeal)
                }
            }

            // Trạng thái lỗi
            if (error != null && !isLoading && reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không thể tải đánh giá: $error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Trạng thái không có đánh giá
            if (reviews.isEmpty() && !isLoading && error == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có đánh giá nào cho sản phẩm này",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray600,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Danh sách đánh giá
            if (reviews.isNotEmpty()) {
                reviews.forEach { review ->
                    ReviewItem(
                        review = review,
                        onImageClick = { imageUrl ->
                            selectedImageUrl = imageUrl
                        }
                    )
                }

                // Nút tải thêm
                if (hasMoreReviews) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = onLoadMore,
                            enabled = !isLoading,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(text = "Xem thêm đánh giá")
                        }
                    }
                }
            }
        }
    }

    // Dialog xem hình ảnh
    if (selectedImageUrl != null) {
        Dialog(onDismissRequest = { selectedImageUrl = null }) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Nút đóng
                IconButton(
                    onClick = { selectedImageUrl = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = Color.White
                    )
                }
            }
        }
    }
}