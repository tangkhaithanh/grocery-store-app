package com.store.grocery_store_app.ui.screens.voucher.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.store.grocery_store_app.data.models.response.VoucherResponse
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VoucherCard(
    voucher: VoucherResponse,
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    val daysLeft = getDaysLeft(voucher.expiryDate)

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = {
                    if (voucher.type == "DISCOUNT") {
                        "https://muagiamgia.com/uploads/stores/16969240611ma-giam-gia-shopee.png"
                    }
                    else {
                        "https://media.licdn.com/dms/image/v2/C5112AQFYNdV_jPUg-g/article-cover_image-shrink_600_2000/article-cover_image-shrink_600_2000/0/1540036078635?e=2147483647&v=beta&t=YwpC3B2YbP47wDutQZxAz1tRMHz1kfSmFRkgrHFm32Y"
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = voucher.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "Hết hạn trong: $daysLeft ngày",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onCheckedChange() }
            )
        }
    }
}
fun getDaysLeft(expiryDate: String): Long {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Date()
        val expiry = sdf.parse(expiryDate)
        val diff = expiry.time - now.time
        TimeUnit.MILLISECONDS.toDays(diff)
    } catch (e: Exception) {
        -1 // lỗi định dạng
    }
}
