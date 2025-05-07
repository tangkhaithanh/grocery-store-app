package com.store.grocery_store_app.ui.screens.reviews

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.store.grocery_store_app.data.models.response.OrderItemResponse
import com.store.grocery_store_app.data.models.response.toOrderItem
import com.store.grocery_store_app.ui.components.ErrorDialog
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.ui.components.SuccessDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewProductScreen(
    orderId : Long,
    orderItemId: Long,
    onNavigateToOrder: () -> Unit,
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var showUserName by remember { mutableStateOf(false) }
    val state by reviewViewModel.state.collectAsState()
    val orderItem by reviewViewModel.orderItem.collectAsState()
    val selectedImages = remember { mutableStateOf<List<Uri>>(emptyList()) }
    LaunchedEffect(Unit) {
        reviewViewModel.loadOrderItemById(orderItemId)
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                selectedImages.value = selectedImages.value + it
            }
        }
    )

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
                        reviewViewModel.createReview(
                            rating = rating,
                            comment = comment,
                            uris = selectedImages.value,
                            orderItemId = orderItemId
                        )
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
                .verticalScroll(rememberScrollState())
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
            orderItem?.toOrderItem(orderId)?.let { OrderItemRow(item = it) }

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

            //Upload Image Review
            Row(
                modifier = Modifier.
                horizontalScroll(rememberScrollState())
            ) {
                selectedImages.value.forEach { uri ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(100.dp)
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Ảnh đánh giá",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Xoá ảnh",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable {
                                    selectedImages.value -= uri
                                },
                            tint = Color.White
                        )
                    }
                }


                if(selectedImages.value.size < 5) {
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


                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Ảnh đánh giá",
                                modifier = Modifier
                                    .alpha(0.2f)
                                    .size(40.dp)
                                    .clickable { launcher.launch("image/*") }
                            )
                            Text(
                                text = "${selectedImages.value.size}/5",
                                modifier = Modifier.alpha(0.2f))

                        }

                    }
                }

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
        // Hiển thị hộp thoại thành công khi tạo đánh giá thành công
        if (state.isReviewed) {
            SuccessDialog(
                title = "Đánh giá thành công",
                content = "Bạn đã đánh giá thành công",
                clearError = reviewViewModel::clearError,
                confirmButtonRequest = onNavigateToOrder
            )
        }
        // Show error if there is one
        if (state.error != null) {
            ErrorDialog(
                title = "Lỗi",
                content = state.error!!.toString(),
                clearError = reviewViewModel::clearError
            )
        }

        // Loading indicator
        LoadingDialog(isLoading = state.isLoading)
    }
}


@Preview(showBackground = true)
@Composable
fun ReviewProductScreenPreview() {
    ReviewProductScreen(
        orderId = 1,
        orderItemId = 1,
        onNavigateToOrder = {}
    )
}