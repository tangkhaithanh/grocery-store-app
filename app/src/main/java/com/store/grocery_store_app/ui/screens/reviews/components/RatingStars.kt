package com.store.grocery_store_app.ui.screens.reviews.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline  // Đảm bảo import đúng

@Composable
fun RatingStars(
    rating: Int,
    maxRating: Int = 5,
    starSize: Int = 16,
    starColor: Color = Color(0xFFFFC107), // Màu vàng cho sao đã chọn
    unselectedStarColor: Color = Color.Gray.copy(alpha = 0.5f), // Màu xám nhạt cho sao chưa chọn
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..maxRating) {
            if (i <= rating) {
                // Sao đã chọn - dùng icon đầy đủ với màu vàng
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier.size(starSize.dp)
                )
            } else {
                // Sao chưa chọn - dùng icon viền với màu xám
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = unselectedStarColor,
                    modifier = Modifier.size(starSize.dp)
                )
            }

            if (i < maxRating) {
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RatingStarsPreviewZero() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text("Rating: 0")
        RatingStars(rating = 0, starSize = 24)
    }
}

@Preview(showBackground = true)
@Composable
fun RatingStarsPreviewOne() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text("Rating: 1")
        RatingStars(rating = 1, starSize = 24)
    }
}