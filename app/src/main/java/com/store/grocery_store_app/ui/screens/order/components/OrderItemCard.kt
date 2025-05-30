package com.store.grocery_store_app.ui.screens.order.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.store.grocery_store_app.R
import com.store.grocery_store_app.data.models.OrderItem
import com.store.grocery_store_app.ui.screens.cart.CartViewModel
import java.text.NumberFormat
import java.util.Locale


@SuppressLint("RememberReturnType")
@Composable
fun OrderItemCard(
    orderItem: OrderItem,
    onNavigateToReviewProduct: (Long,Long) -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    ) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // ✅ Sửa ở đây
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Đặt containerColor để áp dụng chính xác cho Card
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color.Red, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Yêu thích", color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(orderItem.storeName, fontWeight = FontWeight.SemiBold)
                }
                Text("Hoàn thành", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(orderItem.imageRes)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_package)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(orderItem.productName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(orderItem.productDescription,maxLines = 2, color = Color.Gray, fontSize = 12.sp)
                    Text("x${orderItem.quantity}", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    if (orderItem.sellPrice != null && orderItem.sellPrice != orderItem.buyPrice) {
                        Text(
                            text = currencyFormatter.format(orderItem.sellPrice),
                            style = TextStyle(textDecoration = TextDecoration.LineThrough),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Text(currencyFormatter.format(orderItem.buyPrice), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tổng số tiền (${orderItem.quantity} sản phẩm): ${currencyFormatter.format(orderItem.totalAmount)}",
                modifier = Modifier.align(Alignment.End),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (orderItem.canReview == false) {
                    if(orderItem.reviewed==true) {
                        // Nút: Xem đánh giá
                        OutlinedButton(
                            onClick = {
                                onNavigateToProduct(orderItem.productId)
                            },
                        ) {
                            Text("Xem đánh giá")
                        }
                    }


                    Spacer(modifier = Modifier.width(8.dp))

                    // Nút: Mua lại
                    Button(
                        onClick = {
                            onNavigateToProduct(orderItem.productId)
                        }
                    ) {
                        Text("Mua lại")
                    }
                } else if(orderItem.reviewed== false){
                    // Nút: Đánh giá nổi bật
                    Button(
                        onClick = {
                            onNavigateToReviewProduct(
                                orderItem.orderId.toLong(),
                                orderItem.orderItemId.toLong()
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00796B), // Màu xanh nổi bật
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),

                        ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Đánh giá",
                            tint = Color.Yellow
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đánh giá")
                    }
                }


            }
        }
    }
}