package com.store.grocery_store_app.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.store.grocery_store_app.data.models.response.AddressDTO
import com.store.grocery_store_app.data.models.response.CreateOrderResponse
import com.store.grocery_store_app.data.models.response.VoucherResponse
import com.store.grocery_store_app.ui.components.SuccessDialog
import com.store.grocery_store_app.ui.screens.address.Address
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
private val DeepTeal = Color(0xFF004D40)
private val OffWhite = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    products: List<Product>,
    voucher: VoucherResponse?,
    selectedAddress: AddressDTO?,
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onNavigateAddress: () -> Unit = {},
    onNavigateVoucher: () -> Unit = {},
    onNavigateVnPay: () -> Unit = {},
    onOrderSuccess: (CreateOrderResponse) -> Unit = {}
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    val totalGoods = products.sumOf { it.price * it.quantity }
    val shippingFee = 30000
    val discountAmount = voucher?.discount?.toInt() ?: 0
    val shippingDiscount = if (discountAmount > 0) 10000 else 0
    var totalPayment = totalGoods + shippingFee - shippingDiscount - discountAmount
    if(totalPayment < 0) totalPayment = 0

    val defaultAddress by checkoutViewModel.defaultAddress.collectAsState()
    val isLoading by checkoutViewModel.isLoading.collectAsState()
    val error by checkoutViewModel.error.collectAsState()
    var selectedPaymentMethod by remember { mutableStateOf("") }
    val orderSuccess by checkoutViewModel.orderSuccess.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(orderSuccess) {
        if (orderSuccess != null) {
            showSuccessDialog = true
        }
    }

    // Sử dụng địa chỉ đã chọn nếu có, nếu không thì sử dụng địa chỉ mặc định
    val addressToDisplay = selectedAddress ?: defaultAddress
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
                        onClick = {
                            // Validate trước khi đặt hàng
                            when {
                                addressToDisplay == null -> {
                                    checkoutViewModel.showError("Vui lòng chọn địa chỉ giao hàng")
                                    return@Button
                                }

                                selectedPaymentMethod.isEmpty() -> {
                                    checkoutViewModel.showError("Vui lòng chọn phương thức thanh toán")
                                    return@Button
                                }

                                products.isEmpty() -> {
                                    checkoutViewModel.showError("Không có sản phẩm trong đơn hàng")
                                    return@Button
                                }

                                else -> {
                                    // Tất cả validation đã pass, xử lý theo payment method
                                    when (selectedPaymentMethod) {
                                        "COD" -> {
                                            // Thanh toán tiền mặt - tạo order trực tiếp
                                            checkoutViewModel.createOrder(
                                                products = products,
                                                selectedAddress = addressToDisplay,
                                                voucher = voucher,
                                                paymentMethod = selectedPaymentMethod
                                            )
                                        }
                                        "VNPay" -> {
                                            // TODO: Xử lý thanh toán VNPay
                                            onNavigateVnPay()
                                        }
                                        else -> {
                                            checkoutViewModel.showError("Phương thức thanh toán không hợp lệ")
                                        }
                                    }
                                }
                            }
                        },
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SectionTitle("Địa chỉ giao hàng")

                            // Hiển thị nhãn "Mặc định" bên phải tiêu đề nếu địa chỉ được chọn là mặc định
                            if (addressToDisplay?.isDefault == true) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DeepTeal.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "Mặc định",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DeepTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (addressToDisplay != null) {
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${addressToDisplay.userName} - ${addressToDisplay.phoneNumber}",
                                        color = DeepTeal,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${addressToDisplay.streetAddress}, ${addressToDisplay.district}, ${addressToDisplay.city}",
                                        color = DeepTeal,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Thay đổi",
                                    tint = DeepTeal.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            // Hiển thị khi chưa có địa chỉ
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Thêm địa chỉ",
                                    modifier = Modifier.size(24.dp),
                                    tint = DeepTeal
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Thêm địa chỉ giao hàng",
                                    color = DeepTeal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Thêm",
                                    tint = DeepTeal.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
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
                VoucherSection(
                    hasShippingVoucher = voucher?.type == "FREESHIP",
                    discountVoucherAmount = voucher?.discount?.toBigInteger()?.toString(),
                    onVoucherClick = onNavigateVoucher
                )
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
                                    .clickable { selectedPaymentMethod = method }  // Sử dụng selectedPaymentMethod
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = (method == selectedPaymentMethod),  // Sử dụng selectedPaymentMethod
                                    onClick = { selectedPaymentMethod = method },   // Sử dụng selectedPaymentMethod
                                    colors = RadioButtonDefaults.colors(selectedColor = DeepTeal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(method, color = DeepTeal)
                            }
                        }

                        // Hiển thị message nếu chưa chọn payment method
                        if (selectedPaymentMethod.isEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "* Vui lòng chọn phương thức thanh toán",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
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
                        RowInfo("Tổng tiền hàng", currencyFormatter.format(totalGoods))
                        RowInfo("Phí vận chuyển", currencyFormatter.format(shippingFee))
                        RowInfo("Giảm phí vận chuyển", "-${currencyFormatter.format(shippingDiscount)}")
                        RowInfo("Giảm voucher", "-${currencyFormatter.format(discountAmount)}")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = DeepTeal)
                        RowInfo(
                            label = "Tổng thanh toán",
                            value = currencyFormatter.format(totalPayment),
                            valueStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepTeal)
                        )
                    }
                }
            }
        }
        if (showSuccessDialog && orderSuccess != null) {
            SuccessDialog(
                title = "Đặt hàng thành công!",
                content = "Mã đơn hàng: #${orderSuccess!!.orderId}\nTổng tiền: ${
                    NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        .format(orderSuccess!!.totalAmount)
                }",
                onDismissRequest = {
                    showSuccessDialog = false
                    checkoutViewModel.clearOrderSuccess()
                },
                confirmButtonRequest = {
                    showSuccessDialog = false
                    onOrderSuccess(orderSuccess!!)
                    checkoutViewModel.clearOrderSuccess()
                },
                clearError = {
                    showSuccessDialog = false
                    checkoutViewModel.clearOrderSuccess()
                }
            )
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
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
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
            Text(product.price.toString(), color = DeepTeal, fontSize = 13.sp)
        }
        Text(currencyFormatter.format(product.price * product.quantity), color = DeepTeal)
    }
}


data class Product(
    val id: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl : String,
    val flashSaleItemId: Long? = null // Add this field
)


//@Preview(showBackground = true)
//@Composable
//fun CheckoutScreenPreview() {
//    val sampleProducts = listOf(
//        Product(id= 1,name = "Áo thun nam", price = 120000, quantity = 2, imageUrl = ""),
//        Product(id= 2,name = "Quần jean nữ", price = 250000, quantity = 1, imageUrl = ""),
//        Product(id= 3,name = "Giày thể thao", price = 500000, quantity = 1,  imageUrl = "")
//    )
//
//    val sampleVoucher = Voucher(code = "GIAM50K", discountAmount = 50000)
//
//    CheckoutScreen(
//        products = sampleProducts,
//        voucher = sampleVoucher,
//        onPlaceOrderClick = {},
//        onBackClick = {}
//    )
//}

@Preview
@Composable
fun VoucherSectionPreview(

) {
    VoucherSection(
        hasShippingVoucher = true,
        discountVoucherAmount = "123"
    )
}
@Composable
fun VoucherSection(
    hasShippingVoucher: Boolean = true,
    discountVoucherAmount: String? = null,
    onVoucherClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with Voucher title and Navigate Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ConfirmationNumber,
                        contentDescription = "Voucher Icon",
                        tint = Color(0xFFEE4D2D),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shopee Voucher",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hasShippingVoucher) {
                        VoucherChip(
                            text = "Miễn Phí Vận Chuyển",
                            isShippingVoucher = true,
                            onClick = onVoucherClick,
                        )
                    } else if (discountVoucherAmount != null) {
                        VoucherChip(
                            text = "Giảm ${discountVoucherAmount}đ",
                            isShippingVoucher = false,
                            onClick = onVoucherClick
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp).clickable {
                            onVoucherClick()
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(7.dp))

            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
            Spacer(modifier = Modifier.height(7.dp))

            // Coin Usage Row (Không thể sử dụng Xu)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // For the coin icon, you can create a simple circle with an S inside
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFF9A825), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = "Không thể sử dụng Xu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Voucher Chip Component
@Composable
fun VoucherChip(
    text: String,
    isShippingVoucher: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topStart = 4.dp,
                    bottomStart = 4.dp,
                    topEnd = 4.dp,
                    bottomEnd = 4.dp
                )
            )
            .border(
                width = 1.dp,
                color = if (isShippingVoucher) Color(0xFF26AA99) else Color(0xFFEE4D2D),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isShippingVoucher) Color(0xFF26AA99) else Color(0xFFEE4D2D),
            fontSize = 10.sp
        )
    }
}