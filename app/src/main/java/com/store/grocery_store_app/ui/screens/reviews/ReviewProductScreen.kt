package com.store.grocery_store_app.ui.screens.reviews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.R
import com.store.grocery_store_app.data.models.OrderItem
import com.store.grocery_store_app.ui.screens.order.components.OrderItemRow
import com.store.grocery_store_app.ui.screens.reviews.components.RatingStars
import androidx.compose.material3.Switch
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewProductScreen(
    orderItem: OrderItem = OrderItem(
        orderId = "1",
        orderItemId = "1",
        productDescription = "Description",
        canReview = true,
        productName = "Sản phẩm AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        buyPrice = null,
        imageRes = "",
        quantity = 0,
        sellPrice = null,
        storeName = "Grocery App",
        totalAmount = 12
    ),
    orderItemId: Long,
    onNavigateToOrder: () -> Unit,
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var showUserName by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Đánh giá sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToOrder() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay về")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // TODO: handle submit review logic here
                        println("Submit rating=$rating, comment=$comment, showName=$showUserName")
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Gửi đánh giá", tint = androidx.compose.ui.graphics.Color.Red)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Đường cắt ngang
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
            )
            // Sản phẩm
            OrderItemRow(item = orderItem)

            // Đường cắt ngang
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
            )

            // Đánh giá sao
            Column {
                Text(text = "Chất lượng sản phẩm")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStars(
                        rating = rating,
                        isEditable = true,
                        onRatingChanged = { rating = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = when (rating) {
                        5 -> "Tuyệt vời"
                        4 -> "Tốt"
                        3 -> "Ổn"
                        2 -> "Kém"
                        else -> "Tệ"
                    })
                }
            }

            // Ảnh đánh giá
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hình ảnh đánh giá")

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(100.dp) // Kích thước vùng chứa viền
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val strokeWidth = 2.dp.toPx()
                        val dashLength = 10.dp.toPx()
                        val gapLength = 6.dp.toPx()

                        drawRoundRect(
                            color = Color.Gray,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = strokeWidth,
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                    floatArrayOf(dashLength, gapLength),
                                    0f
                                )
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Ảnh đánh giá",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .alpha(0.6f)
                            .size(48.dp)
                    )
                }

                Text(text = "5/5", modifier = Modifier.alpha(0.6f))
            }


            // Nhận xét
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Nhận xét") },
                modifier = Modifier.fillMaxWidth()
                    .height(150.dp)
                    .background(color = Color(0xFFF5F5F2)),
            )

            // Hiển thị tên người dùng
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f) // Chiếm phần lớn không gian
                        .padding(end = 8.dp)
                ) {
                    Text("Hiển thị tên người đăng nhập", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = if (showUserName)
                            "Tên tài khoản của bạn sẽ hiển thị như ABCXYZZ"
                        else
                            "Ẩn tên người đăng nhập trên đánh giá này",
                        maxLines = 2,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Switch(
                    checked = showUserName,
                    onCheckedChange = { showUserName = it }
                )
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewProductScreenPreview() {
    ReviewProductScreen(
        orderItemId = 1,
        onNavigateToOrder = {}
    )
}