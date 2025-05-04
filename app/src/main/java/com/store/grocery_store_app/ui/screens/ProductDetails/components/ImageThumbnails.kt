package com.store.grocery_store_app.ui.screens.ProductDetails.components
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.store.grocery_store_app.ui.theme.DeepTeal
/**
 * Row of image thumbnails for quick navigation
 */
@Composable
fun ImageThumbnails(
    images: List<String?>,
    currentImageIndex: Int,
    onThumbnailClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(images) { index, imageUrl ->
            val borderWidth by animateDpAsState(
                targetValue = if (index == currentImageIndex) 2.dp else 0.dp,
                label = "Border Animation"
            )

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = borderWidth,
                        color = DeepTeal,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(Color(0xFFF5F5F5))
                    .clickable { onThumbnailClick(index) },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = DeepTeal
                                )
                            }
                            is AsyncImagePainter.State.Success -> {
                                SubcomposeAsyncImageContent(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBasket,
                                    contentDescription = null,
                                    tint = DeepTeal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingBasket,
                        contentDescription = null,
                        tint = DeepTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}