package com.store.grocery_store_app.ui.screens.FlashSale

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.screens.FavoriteProduct.FavoriteProductViewModel
import com.store.grocery_store_app.ui.screens.home.ProductViewModel
import com.store.grocery_store_app.ui.screens.home.components.ProductSection
import java.text.NumberFormat
import java.util.Locale




@Composable
fun FlashSaleSection(
    title: String, // Tiêu đề của Flash Sale Section (ví dụ: "Flash Sale 00:00 - 12:00")
    listProduct: List<ProductResponse>, // Danh sách sản phẩm đã được chuẩn bị cho FlashSale này
    favouriteViewModel: FavoriteProductViewModel = hiltViewModel(), // Vẫn cần cho ProductCard
    isLoading: Boolean = false, // Thêm cờ loading nếu việc lấy listProduct là bất đồng bộ
    error: String? = null,      // Thêm thông báo lỗi nếu có
    onSeeMoreClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    onAddToCartClick: (ProductResponse) -> Unit,
    icon: ImageVector = Icons.Default.Bolt, // Icon mặc định cho Flash Sale
    iconTint: Color = Color(0xFFFF6F00),    // Màu cam cho icon Flash Sale
    headerColor: Color = Color(0xFFBF360C)  // Màu đỏ đậm cho header Flash Sale
) {
    // Không cần productViewModel.state ở đây nữa nếu listProduct, isLoading, error được truyền từ bên ngoài
    // val state by productViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        // Section header (tương tự ProductSection)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Flash Sale Icon",
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = headerColor
                    )
                )
            }
            // Nút "Xem thêm" có thể không cần thiết cho từng FlashSale item riêng lẻ
            // nếu bạn có một trang "Tất cả Flash Sale". Tùy bạn quyết định.
            if (listProduct.size > 4) { // Chỉ hiển thị "Xem thêm" nếu có nhiều hơn X sản phẩm
                Button(
                    onClick = onSeeMoreClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFE57373) // Màu cho text "See more"
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Xem thêm",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Xử lý trạng thái Loading và Error (tương tự ProductSection)
        if (isLoading && listProduct.isEmpty()) { // Chỉ hiển thị loading nếu list rỗng
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Chiều cao tương ứng với ProductCard
            ) {
                CircularProgressIndicator(color = iconTint)
            }
        } else if (error != null && listProduct.isEmpty()) { // Chỉ hiển thị error nếu list rỗng
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Không thể tải sản phẩm Flash Sale: $error",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
            }
        } else if (listProduct.isEmpty() && !isLoading) { // Trường hợp list rỗng và không loading/error
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // Padding ngang cho Card
                    .heightIn(min = 100.dp), // Chiều cao tối thiểu để Card không quá nhỏ
                shape = RoundedCornerShape(8.dp), // Bo góc cho Card
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Màu nền mặc định của Card, hoặc bạn có thể set màu khác
                    // containerColor = Color.White // Nếu bạn muốn Card trắng và có shadow
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Thêm độ nổi cho Card
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp) // Padding bên trong Card cho Text
                ) {
                    Text(
                        text = "Hiện tại chưa có chương trình Flash Sale nào diễn ra.", // Hoặc thông báo lỗi chung
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.Red // Giữ màu đỏ cho chữ lỗi để nổi bật
                    )
                }
            }
        } else if (listProduct.isNotEmpty()) {
            // Products row (LazyRow)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa các ProductCard
            ) {
                items(listProduct, key = { it.id }) { product -> // Thêm key để tối ưu recomposition
                    ProductCard(
                        product = product,
                        favouriteViewModel = favouriteViewModel,
                        onProductClick = { onProductClick(product.id) },
                        onAddToCartClick = { onAddToCartClick(product) },
                        isFlashSale = true // ĐÁNH DẤU ĐÂY LÀ SẢN PHẨM FLASH SALE
                    )
                }
            }
        }
    }
}
@Composable
fun ProductCard(
    product: ProductResponse,
    favouriteViewModel: FavoriteProductViewModel,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    isFlashSale: Boolean = false, // Thêm tham số này
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    Card(
        modifier = modifier
            .width(160.dp) // Chiều rộng cố định cho card
            .clickable(onClick = onProductClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box { // Box để đặt nhãn FlashSale lên trên ảnh
            Column {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(), // Lấy ảnh đầu tiên
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // ... (Hiển thị giá, đánh giá, nút thêm vào giỏ hàng...)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currencyFormatter.format(product.effectivePrice), // Giả sử bạn có hàm formatCurrency
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        // Nút thêm vào giỏ hàng hoặc icon yêu thích ở đây
                    }
                }
            }

            // Nhãn Flash Sale
            if (isFlashSale) {
                FlashSaleLabel(modifier = Modifier.align(Alignment.TopStart))
            }
        }
    }
}

@Composable
fun FlashSaleLabel(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .padding(4.dp), // Padding nhỏ để không sát viền
        color = Color.Red, // Màu nền cho nhãn
        shape = RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp) // Bo góc chéo
    ) {
        Text(
            text = "FLASH SALE",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
