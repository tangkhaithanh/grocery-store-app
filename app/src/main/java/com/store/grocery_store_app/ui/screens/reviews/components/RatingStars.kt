package com.store.grocery_store_app.ui.screens.reviews.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Int,
    maxRating: Int = 5,
    starSize: Int = 24,
    starColor: Color = Color(0xFFFFC107),
    unselectedStarColor: Color = Color.Gray.copy(alpha = 0.5f),
    isEditable: Boolean = false,
    onRatingChanged: (Int) -> Unit = {}
) {
    Row {
        for (i in 1..maxRating) {
            val starModifier = if (isEditable) {
                Modifier
                    .size(starSize.dp)
                    .clickable { onRatingChanged(i) }
            } else {
                Modifier.size(starSize.dp)
            }

            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Star $i",
                tint = if (i <= rating) starColor else unselectedStarColor,
                modifier = starModifier
            )

            if (i < maxRating) {
                Spacer(modifier = Modifier.width(2.dp))
            }
        }
    }
}
