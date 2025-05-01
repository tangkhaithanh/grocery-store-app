package com.store.grocery_store_app.ui.screens.order.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.store.grocery_store_app.data.models.OrderItem


@Composable
fun OrderItemRow(item: OrderItem) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.productDescription, color = Color.Gray, fontSize = 12.sp)
            Text("x${item.quantity}", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(horizontalAlignment = Alignment.End) {
            if (item.sellPrice != null) {
                Text(
                    text = "₫${item.sellPrice}",
                    style = TextStyle(textDecoration = TextDecoration.LineThrough),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Text("₫${item.buyPrice}", fontWeight = FontWeight.Bold)
        }
    }
}