package com.store.grocery_store_app.ui.screens.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.data.models.OrderGroup

@Composable
fun OrderGroupCard(group: OrderGroup, tab : String) {
    var expanded by remember { mutableStateOf(false) }
    val status = when (tab) {
        "Chờ xác nhận" -> "Chờ thanh toán"
        "Chờ lấy hàng" -> "Đang lấy hàng"
        "Chờ giao hàng" -> "Đang giao hàng"
        "Đã giao" -> "Hoàn thành"
        "Đã huỷ" -> "Đã huỷ"
        else -> "Không xác định"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.Red // Đặt containerColor để áp dụng chính xác cho Card
//        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(group.storeName, fontWeight = FontWeight.SemiBold)
                Text(status, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Luôn hiển thị item đầu tiên
            OrderItemRow(group.items.first())

            // Nếu có nhiều hơn 1 sản phẩm và chưa expanded -> hiển thị nút
            if (group.items.size > 1) {
                if (expanded) {
                    group.items.drop(1).forEach {
                        Spacer(modifier = Modifier.height(6.dp))
                        OrderItemRow(it)
                    }
                }

                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (expanded) "Thu gọn" else "Xem thêm (${group.items.size - 1}) sản phẩm")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Tổng số tiền (${group.items.sumOf { it.quantity }} sản phẩm): ₫${group.totalAmount}",
                modifier = Modifier.align(Alignment.End),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { }) { Text("Liên hệ Shop") }
            }
        }
    }
}

