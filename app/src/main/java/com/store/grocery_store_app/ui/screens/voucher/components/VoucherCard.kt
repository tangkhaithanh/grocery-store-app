package com.store.grocery_store_app.ui.screens.voucher.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.store.grocery_store_app.data.models.response.VoucherResponse
import java.text.SimpleDateFormat
// import java.time.temporal.ChronoUnit // Không được sử dụng
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val DeepTeal = Color(0xFF004D40)
// private val FreeShipBg = DeepTeal.copy(alpha = 0.1f) // Không được sử dụng
// private val DiscountBg = DeepTeal.copy(alpha = 0.2f) // Không được sử dụng

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VoucherCard(
    voucher: VoucherResponse,
    currentSelectedId: Long?, // Nhận ID hiện tại đang được chọn
    onVoucherClick: () -> Unit    // Đổi tên prop
) {
    val daysLeft = getDaysLeft(voucher.expiryDate)
    val iconUrl = if (voucher.type == "DISCOUNT")
        "https://muagiamgia.com/uploads/stores/16969240611ma-giam-gia-shopee.png"
    else
        "https://media.licdn.com/dms/image/v2/C5112AQFYNdV_jPUg-g/article-cover_image-shrink_600_2000/article-cover_image-shrink_600_2000/0/1540036078635?e=2147483647&v=beta&t=YwpC3B2YbP47wDutQZxAz1tRMHz1kfSmFRkgrHFm32Y"

    val isChecked = voucher.id == currentSelectedId // Tính toán isChecked ở đây

    // Log để kiểm tra
    // LaunchedEffect(isChecked, voucher.id) { // Sử dụng LaunchedEffect để log khi isChecked thay đổi
    //     Log.d("VoucherCard", "Voucher: ${voucher.name} (ID: ${voucher.id}), currentSelectedId: $currentSelectedId, isChecked: $isChecked")
    // }


    Card(
        shape = RoundedCornerShape(corner = CornerSize(6.dp)),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = voucher.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = DeepTeal
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Hết hạn trong: $daysLeft ngày",
                        style = MaterialTheme.typography.bodySmall,
                        color = DeepTeal.copy(alpha = 0.7f)
                    )
                }
                Checkbox(
                    checked = isChecked, // Sử dụng isChecked đã tính toán
                    onCheckedChange = { _ -> // Tham số boolean từ Checkbox không cần thiết ở đây
                        onVoucherClick()     // Gọi hàm callback khi click
                    },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = Color.White,
                        uncheckedColor = DeepTeal,
                        checkedColor = DeepTeal
                    )
                )
            }

            // Quantity Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 8.dp)
                    .background(
                        color = Color(0xFFFFE8E8),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "x${voucher.quantity}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFFE53935)
                )
            }
        }
    }
}

fun getDaysLeft(expiryDate: String): Long {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Date()
        val expiry = sdf.parse(expiryDate) ?: return -1 // Trả về -1 nếu parse lỗi
        val diff = expiry.time - now.time
        TimeUnit.MILLISECONDS.toDays(diff)
    } catch (e: Exception) {
        Log.e("VoucherCard", "Error parsing expiry date: $expiryDate", e)
        -1
    }
}