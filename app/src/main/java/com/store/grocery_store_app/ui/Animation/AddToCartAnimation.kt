package com.store.grocery_store_app.ui.Animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.store.grocery_store_app.ui.theme.DeepTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun AddToCartAnimation(
    imageUrl: String?,       // URL hình ảnh sản phẩm (nếu có)
    isPlaying: Boolean,      // Cờ kích hoạt animation
    sourcePosition: Offset,  // Toạ độ bắt đầu (vị trí nút AddToCart)
    cartPosition: Offset,    // Toạ độ kết thúc (nút giỏ hàng)
    onAnimationEnd: () -> Unit
) {
    // Nếu không được kích hoạt hoặc chưa có vị trí thì không vẽ gì
    if (!isPlaying) return

    // Tính khoảng cách X, Y
    val xDistance = cartPosition.x - sourcePosition.x
    val yDistance = cartPosition.y - sourcePosition.y

    // Tính độ dài đường bay
    val distance = sqrt(xDistance * xDistance + yDistance * yDistance)

    // Tuỳ chỉnh thời gian dựa trên độ dài
    val durationMs = (700 + distance / 5).toInt().coerceIn(800, 1000)

    // Trạng thái cho va chạm
    var showImpact by remember { mutableStateOf(false) }

    // Các Animatable
    val progress = remember { Animatable(0f) }  // Tiến trình từ 0->1
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(1f) }
    val impactScale = remember { Animatable(0f) }

    // Easing tuyến tính (bay thẳng một tốc độ)
    val linearEasing = LinearEasing
    val animationSpec = remember {
        tween<Float>(
            durationMillis = durationMs,
            easing = linearEasing
        )
    }

    // Chạy animation khi isPlaying = true
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            // Reset giá trị ban đầu
            progress.snapTo(0f)
            alpha.snapTo(1f)
            scale.snapTo(1f)
            impactScale.snapTo(0f)
            showImpact = false

            // Bay đến vị trí giỏ
            launch {
                // Animate di chuyển
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = animationSpec
                )

                // Chạm đến giỏ => bắt đầu hiệu ứng va chạm nhẹ
                delay(50) // chờ 1 chút để nhìn rõ

                showImpact = true

                // Zoom "impact" lên
                impactScale.animateTo(
                    targetValue = 2.0f,
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = FastOutSlowInEasing
                    )
                )

                // Mờ dần hình sản phẩm
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(150)
                )

                // Thu impact lại
                impactScale.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = FastOutSlowInEasing
                    )
                )

                // Kết thúc animation
                onAnimationEnd()
            }
        }
    }

    // Vẽ phần tử bay
    Box {
        val t = progress.value
        val x = sourcePosition.x + (xDistance * t)
        val y = sourcePosition.y + (yDistance * t)

        val itemSize = 50.dp
        val half = 25 // nửa kích thước (px)

        Box(
            modifier = Modifier
                .offset { IntOffset(x.roundToInt() - half, y.roundToInt() - half) }
                .size(itemSize)
                .scale(scale.value)
                .alpha(alpha.value)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            if (!imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Product",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback icon
                Icon(
                    imageVector = Icons.Default.ShoppingBasket,
                    contentDescription = null,
                    tint = DeepTeal,
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                )
            }
        }

        // Impact effect
        if (showImpact) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            cartPosition.x.roundToInt() - 30,
                            cartPosition.y.roundToInt() - 30
                        )
                    }
                    .size(60.dp)
                    .scale(impactScale.value)
                    .alpha(1f - impactScale.value / 2.0f)
                    .background(Color(0x80FF5722), CircleShape)
            )
        }
    }
}