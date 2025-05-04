package com.store.grocery_store_app.ui.screens.ProductDetails.components
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
 * Main image carousel for the product
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductImageCarousel(
    images: List<String?>,
    currentImageIndex: Int,
    onImageChange: (Int) -> Unit,
    productName: String
) {
    val pagerState = rememberPagerState(
        initialPage = currentImageIndex,
        pageCount = { images.size }
    )
    val scope = rememberCoroutineScope()

    // Update ViewModel when page changes
    LaunchedEffect(pagerState.currentPage) {
        if (currentImageIndex != pagerState.currentPage) {
            onImageChange(pagerState.currentPage)
        }
    }

    // Update pager when thumbnails are clicked
    LaunchedEffect(currentImageIndex) {
        if (pagerState.currentPage != currentImageIndex) {
            pagerState.animateScrollToPage(currentImageIndex)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                if (images[page] != null) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(images[page])
                            .crossfade(true)
                            .build(),
                        contentDescription = productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = DeepTeal
                                )
                            }
                            is AsyncImagePainter.State.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBasket,
                                        contentDescription = null,
                                        tint = DeepTeal,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }
                            is AsyncImagePainter.State.Success -> {
                                SubcomposeAsyncImageContent()
                            }
                            else -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBasket,
                                        contentDescription = null,
                                        tint = DeepTeal,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingBasket,
                        contentDescription = null,
                        tint = DeepTeal,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) DeepTeal else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}
