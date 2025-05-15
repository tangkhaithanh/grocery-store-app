package com.store.grocery_store_app.ui.screens.order.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.data.models.OrderGroup
import com.store.grocery_store_app.data.models.StatusOrderType
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderGroupCard(
    group: OrderGroup,
    tab : String,
    onNavigateToProductDetails: (Long) -> Unit,
    onNavigateToDeliveryDetail: (String) -> Unit = {},
    onCancelOrder: (Long) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val status = when (tab) {
        "Chờ xác nhận" -> "Chờ xác nhận"
        "Chờ lấy hàng" -> "Đang lấy hàng"
        "Chờ giao hàng" -> "Đang giao hàng"
        "Đã giao" -> "Hoàn thành"
        "Đã huỷ" -> "Đã huỷ"
        else -> "Không xác định"
    }
    var showConfirmDialog by remember { mutableStateOf(false) }
    // Confirmation dialog for delete
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn hủy đơn hàng này?") },
            confirmButton = {
                TextButton(onClick = {
                    onCancelOrder(group.orderId.toLong())
                    showConfirmDialog = false
                }) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
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
            if(tab == "Chờ xác nhận"|| tab =="Đã huỷ"){
                OrderItemRow(group.items.first(), onNavigateToProductDetails )
            }
            else {
                OrderItemRow(group.items.first())
            }
            // Nếu có nhiều hơn 1 sản phẩm và chưa expanded -> hiển thị nút
            if (group.items.size > 1) {
                if (expanded) {
                    group.items.drop(1).forEach {
                        Spacer(modifier = Modifier.height(6.dp))
                        OrderItemRow(it, onNavigateToProductDetails)
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
                "Tổng số tiền (${group.items.sumOf { it.quantity }} sản phẩm): ${currencyFormatter.format(group.totalAmount)}",
                modifier = Modifier.align(Alignment.End),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                if(tab == "Chờ xác nhận") {
                    OutlinedButton(
                        onClick = { showConfirmDialog = true },
                        border = BorderStroke(1.dp, Color.Red)
                    ) { Text("Hủy đơn hàng", style = TextStyle(color = Color.Red))}
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { }) { Text("Liên hệ Shop") }
                }
                else if(tab == "Chờ lấy hàng") {
                    OutlinedButton(onClick = { }) { Text("Liên hệ Shop") }
                }
                else if(tab == "Chờ giao hàng") {
                    OutlinedButton(onClick = { onNavigateToDeliveryDetail(group.orderId) }) { Text("Xem đơn hàng") }
                }
                else if(tab == "Đã huỷ") {
                    Button (onClick = { }) { Text("Mua lại") }
                }
            }
        }
    }
}

