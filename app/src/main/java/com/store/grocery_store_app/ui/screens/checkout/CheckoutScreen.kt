package com.store.grocery_store_app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.store.grocery_store_app.ui.screens.address.Address

@OptIn(ExperimentalMaterial3Api::class)
private val DeepTeal = Color(0xFF004D40)
private val OffWhite = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    products: List<Product>,
    voucher: Voucher?,
    onBackClick: () -> Unit = {},
    onPlaceOrderClick: () -> Unit = {},
    onNavigateAddress: () -> Unit = {},
    onNavigateVoucher: () -> Unit = {}
) {
    val totalGoods = products.sumOf { it.price * it.quantity }
    val shippingFee = 30000
    val discountAmount = voucher?.discountAmount ?: 0
    val shippingDiscount = if (discountAmount > 0) 10000 else 0
    val totalPayment = totalGoods + shippingFee - shippingDiscount - discountAmount

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thanh toán", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepTeal
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Tổng thanh toán
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tổng thanh toán:",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepTeal
                        )
                        Text(
                            text = "${totalPayment}đ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepTeal
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onPlaceOrderClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepTeal,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Đặt hàng")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. Địa chỉ giao hàng
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onNavigateAddress()
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        SectionTitle("Địa chỉ giao hàng")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Địa chỉ",
                                modifier = Modifier.size(24.dp),
                                tint = DeepTeal
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Nguyễn Văn A - 0901234567", color = DeepTeal)
                                Text("123 Lý Thường Kiệt, Quận 10, TP.HCM", color = DeepTeal)
                            }
                        }
                    }
                }
            }

            // 2. Danh sách sản phẩm
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        SectionTitle("Sản phẩm")
                        products.forEach { product ->
                            ProductItem(product)
                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = DeepTeal)
                        }
                    }
                }
            }

            // 3. Mã giảm giá & Vận chuyển
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        SectionTitle("Mã giảm giá & Vận chuyển")
                        if (voucher != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.ConfirmationNumber,
                                    contentDescription = "Voucher",
                                    tint = DeepTeal
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${voucher.code} - Giảm ${voucher.discountAmount}đ", color = DeepTeal)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Phí vận chuyển: ${shippingFee}đ", style = MaterialTheme.typography.bodyMedium, color = DeepTeal)
                    }
                }
            }

            // 4. Phương thức thanh toán
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        SectionTitle("Phương thức thanh toán")
                        val paymentMethods = listOf("VNPay", "COD")
                        var selected by remember { mutableStateOf(paymentMethods[0]) }
                        paymentMethods.forEach { method ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selected = method }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = (method == selected),
                                    onClick = { selected = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = DeepTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(method, color = DeepTeal)
                            }
                        }
                    }
                }
            }

            // 5. Chi tiết thanh toán
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        SectionTitle("Chi tiết thanh toán")
                        RowInfo("Tổng tiền hàng", "${totalGoods}đ")
                        RowInfo("Phí vận chuyển", "${shippingFee}đ")
                        RowInfo("Giảm phí vận chuyển", "-${shippingDiscount}đ")
                        RowInfo("Giảm voucher", "-${discountAmount}đ")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = DeepTeal)
                        RowInfo(
                            label = "Tổng thanh toán",
                            value = "${totalPayment}đ",
                            valueStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepTeal)
                        )
                    }
                }
            }
        }
    }
}

// ---------- Composable phụ ----------

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = DeepTeal,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun RowInfo(
    label: String,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(color = DeepTeal)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = DeepTeal)
        Text(text = value, style = valueStyle)
    }
}

@Composable
fun ProductItem(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(6.dp))
        ) {
            AsyncImage(
                model = if(product.imageUrl.isEmpty()) "https://onelife.vn/_next/image?url=https%3A%2F%2Fstorage.googleapis.com%2Fsc_pcm_product%2Fprod%2F2023%2F12%2F15%2F19248-8936079121822.jpg&w=1920&q=75"
                else product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontWeight = FontWeight.Medium, color = DeepTeal)
            Text("x${product.quantity}", color = DeepTeal, fontSize = 13.sp)
        }
        Text("${product.price * product.quantity}đ", color = DeepTeal)
    }
}


data class Product(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl : String
)

data class Voucher(
    val code: String,
    val discountAmount: Int
)
@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    val sampleProducts = listOf(
        Product(id= 1,name = "Áo thun nam", price = 120000, quantity = 2, imageUrl = ""),
        Product(id= 2,name = "Quần jean nữ", price = 250000, quantity = 1, imageUrl = ""),
        Product(id= 3,name = "Giày thể thao", price = 500000, quantity = 1,  imageUrl = "")
    )

    val sampleVoucher = Voucher(code = "GIAM50K", discountAmount = 50000)

    CheckoutScreen(
        products = sampleProducts,
        voucher = sampleVoucher,
        onPlaceOrderClick = {},
        onBackClick = {}
    )
}
