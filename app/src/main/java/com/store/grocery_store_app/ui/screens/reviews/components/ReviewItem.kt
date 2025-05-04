package com.store.grocery_store_app.ui.screens.reviews.components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.ui.theme.Gray600
import java.text.SimpleDateFormat
import java.util.*
/**
 * Component hiển thị một đánh giá
 */
@Composable
fun ReviewItem(
    review: ReviewResponse,
    onImageClick: (String) -> Unit
) {
    // Parse createdAt từ String sang Date để format
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = try {
        val date = dateFormat.parse(review.createdAt)
        displayFormat.format(date)
    } catch (e: Exception) {
        review.createdAt
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Thông tin người dùng và đánh giá
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar người dùng
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://ui-avatars.com/api/?name=${review.userFullName.replace(" ", "+")}&background=random")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Thông tin người dùng
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = review.userFullName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                RatingStars(rating = review.rating)
            }
            // Ngày đánh giá
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )
        }

        // Nội dung đánh giá
        if (review.comment.isNotBlank()) {
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        // Hình ảnh đánh giá
        if (review.imageUrls.isNotEmpty()) {
            ReviewImageGallery(
                images = review.imageUrls,
                onImageClick = onImageClick
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = Color(0xFFEEEEEE)
        )
    }
}
