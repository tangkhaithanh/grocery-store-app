package com.store.grocery_store_app.ui.screens.order.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.store.grocery_store_app.R
import com.store.grocery_store_app.data.models.OrderItem
import java.text.NumberFormat
import java.util.Locale


@Composable
fun OrderItemRow(item: OrderItem, onNavigateWhenClickImage: (Long) -> Unit = {}) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageRes)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                    onNavigateWhenClickImage(item.productId)
                },
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_package)
        )


        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.productDescription, color = Color.Gray, fontSize = 12.sp,maxLines =2)
            if(item.quantity > 0) {
                Text("x${item.quantity}", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(horizontalAlignment = Alignment.End) {
            if (item.sellPrice != null && item.buyPrice != item.sellPrice) {
                Text(
                    text = currencyFormatter.format(item.sellPrice),
                    style = TextStyle(textDecoration = TextDecoration.LineThrough),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            if(item.buyPrice != null ) {
                Text(currencyFormatter.format(item.buyPrice), fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OrderItemPreview() {
    OrderItemRow(item = OrderItem(
        orderId = "1",
        orderItemId = "1",
        productId = 1,
        productDescription = "Description",
        canReview = true,
        productName = "Sản phẩm AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
        buyPrice = null,
        imageRes = "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75",
        quantity = 0,
        sellPrice = null,
        storeName = "Grocery App",
        totalAmount = 12,
        reviewed = true
    )
    )
}