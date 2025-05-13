package com.store.grocery_store_app.ui.screens.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.grocery_store_app.data.models.response.CartItemResponse

@Composable
fun CartItemCard(
    cartItem: CartItemResponse,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onRemove:(Long) -> Unit,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // ✅ Sửa ở đây
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF) // Đặt containerColor để áp dụng chính xác cho Card
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
                    Text("Grocery", fontWeight = FontWeight.SemiBold)
                }
                Text("Sửa", color = Color.Black, modifier = Modifier.alpha(0.5f), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            SwipeableCartItemRow(
                cartItem = cartItem,
                checked = checked,
                onDelete = onRemove,
                onShowSimilar = {},
                onCheckedChange = onCheckedChange,
                onQuantityIncrease = onQuantityIncrease,
                onQuantityDecrease = onQuantityDecrease
            )
        }
    }
}