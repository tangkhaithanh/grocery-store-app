package com.store.grocery_store_app.ui.screens.order.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.grocery_store_app.R
import com.store.grocery_store_app.data.models.OrderItem


@Composable
fun OrderItemCard(order: OrderItem) {
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
                    Text(order.storeName, fontWeight = FontWeight.SemiBold)
                }
                Text("Hoàn thành", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_package),
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(order.productName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(order.productDescription, color = Color.Gray, fontSize = 12.sp)
                    Text("x${order.quantity}", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    if (order.sellPrice != null) {
                        Text(
                            text = "₫${order.sellPrice}",
                            style = TextStyle(textDecoration = TextDecoration.LineThrough),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Text("₫${order.buyPrice}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tổng số tiền (${order.quantity} sản phẩm): ₫${order.totalAmount}",
                modifier = Modifier.align(Alignment.End),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (order.canReview == false) {
                    // Nút: Xem đánh giá
                    OutlinedButton(
                        onClick = { /* TODO: Review */ },
                    ) {
                        Text("Xem đánh giá")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Nút: Mua lại
                    Button(
                        onClick = { /* TODO: Buy again */ }
                    ) {
                        Text("Mua lại")
                    }
                } else {
                    // Nút: Đánh giá nổi bật
                    Button(
                        onClick = { /* TODO: Review */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3), // Màu xanh nổi bật
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),

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