package com.store.grocery_store_app.ui.screens.vnpay
// src/main/java/com/yourpackage/vnpayonly/VnPayOnlyScreen.kt

import android.annotation.SuppressLint
import android.app.Activity // Cần thiết nếu bạn dùng context as Activity, nhưng ở đây không trực tiếp
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext // Không dùng trực tiếp trong ví dụ này
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.utils.VnPayConstants
import java.text.NumberFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VnPayOnlyScreen(
    totalAmount: Long = 1,
    viewModel: VnPayOnlyViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateOrder: () -> Unit = {}
) {
    val vnPayState by viewModel.vnPayPaymentState.collectAsState()
    var showWebViewState by remember { mutableStateOf(false) } // Đổi tên để phân biệt với tham số của VnPayWebViewInScreen
    var vnPayUrlToLoadState by remember { mutableStateOf("") } // Đổi tên
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    LaunchedEffect(vnPayState) {
        when (val state = vnPayState) {
            is VnPayPaymentState.UrlGenerated -> {
                vnPayUrlToLoadState = state.paymentUrl
                showWebViewState = true // Chỉ đặt showWebViewState ở đây
                Log.i("VnPayOnlyScreen", "URL Generated, showing WebView: ${state.paymentUrl}")
            }
            is VnPayPaymentState.PaymentSuccess,
            is VnPayPaymentState.PaymentFailed,
            is VnPayPaymentState.PaymentCancelled,
            is VnPayPaymentState.Error,
            is VnPayPaymentState.Idle -> {
                // Nếu muốn ẩn WebView sau khi có kết quả, đặt showWebViewState = false ở đây
                if (state !is VnPayPaymentState.UrlGenerated && state !is VnPayPaymentState.GeneratingUrl && state !is VnPayPaymentState.ProcessingReturn) {
                    showWebViewState = false
                }
            }
            else -> { /* GeneratingUrl, ProcessingReturn handled by indicators */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh Toán VNPay (Sandbox)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (showWebViewState) { // Kiểm tra trạng thái showWebView của màn hình này
                            showWebViewState = false // Yêu cầu đóng WebView
                            viewModel.resetPaymentState() // Reset trạng thái thanh toán
                        } else {
                            onNavigateBack() // Nếu không có WebView, thực hiện navigate back
                        }
                    }) {
                        Icon(androidx.compose.material.icons.Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // --- Nội dung của Column giữ nguyên như trước ---
                when (val state = vnPayState) {
                    is VnPayPaymentState.Idle -> {
                        Button(onClick = {
                            val amount = totalAmount
                            val description = "Thanh toan VNPAY #${viewModel.generateLocalTxnRef().takeLast(6)}"
                            viewModel.initiateVnPayPayment(amount, description)
                        }) {
                            Text("Thanh toán ${currencyFormatter.format(totalAmount)} bằng VNPay")
                        }
                    }
                    is VnPayPaymentState.GeneratingUrl -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Đang chuẩn bị thanh toán VNPay...")
                    }
                    is VnPayPaymentState.UrlGenerated -> {
                        Text("Mã đơn hàng: ${state.appTxnRef}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Đang chuyển đến cổng thanh toán VNPay...")
                        // Không cần CircularProgressIndicator ở đây nữa nếu WebView hiển thị ngay
                    }
                    is VnPayPaymentState.ProcessingReturn -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.message)
                    }
                    is VnPayPaymentState.PaymentSuccess -> {
                        PaymentResultCard(
                            icon = Icons.Filled.CheckCircle,
                            iconColor = Color(0xFF4CAF50), // Màu xanh lá cây
                            title = "THÀNH CÔNG!",
                            message = state.message,
                            details = listOf("Mã giao dịch: ${state.txnRef}"),
                            buttonText = "Tuyệt vời!\n Đóng",
                            onButtonClick = { viewModel.resetPaymentState(); onNavigateOrder() }
                        )
                    }
                    is VnPayPaymentState.PaymentFailed -> {
                        PaymentResultCard(
                            icon = Icons.Filled.Error,
                            iconColor = MaterialTheme.colorScheme.error,
                            title = "THẤT BẠI!",
                            message = state.message,
                            details = listOf(
                                "Mã đơn hàng: ${state.txnRef ?: "N/A"}",
                                "Mã lỗi VNPay: ${state.responseCode ?: "N/A"}"
                            ),
                            buttonText = "Thử lại",
                            onButtonClick = { viewModel.resetPaymentState() }
                        )
                    }
                    is VnPayPaymentState.PaymentCancelled -> {
                        PaymentResultCard(
                            icon = Icons.Filled.HighlightOff,
                            iconColor = Color.Gray,
                            title = "ĐÃ HỦY",
                            message = state.message,
                            details = emptyList(), // Hoặc "Giao dịch đã được hủy bởi người dùng."
                            buttonText = "Thử lại / Đóng",
                            onButtonClick = { viewModel.resetPaymentState() } // Hoặc onNavigateBack()
                        )
                    }
                    is VnPayPaymentState.Error -> {
                        PaymentResultCard(
                            icon = Icons.Filled.Info, // Hoặc Error
                            iconColor = MaterialTheme.colorScheme.error,
                            title = "LỖI HỆ THỐNG!",
                            message = state.message,
                            details = emptyList(),
                            buttonText = "OK",
                            onButtonClick = { viewModel.resetPaymentState() }
                        )
                    }
                }
            } // End Column

            // Hiển thị VnPay WebView nếu cần
            if (showWebViewState && vnPayUrlToLoadState.isNotEmpty()) { // Sử dụng state của VnPayOnlyScreen
                VnPayWebViewInScreen(
                    url = vnPayUrlToLoadState, // Truyền URL từ state
                    isVisible = showWebViewState, // Truyền trạng thái hiển thị
                    onShouldOverrideUrlLoading = { _, urlString ->
                        Log.d("VnPayOnlyScreen_WebView", "WebView URL Check: $urlString")
                        if (urlString != null && urlString.startsWith(VnPayConstants.getReturnUrl())) {
                            // Không set showWebViewState = false ở đây nữa, LaunchedEffect sẽ xử lý dựa trên vnPayState
                            val uri = Uri.parse(urlString)
                            val paramsMap = mutableMapOf<String, String?>()
                            uri.queryParameterNames.forEach { key ->
                                paramsMap[key] = uri.getQueryParameter(key)
                            }
                            viewModel.handleVnPayReturn(paramsMap)
                            return@VnPayWebViewInScreen true
                        }
                        return@VnPayWebViewInScreen false
                    },
                    onWebViewClosedOrCancelled = { // Callback mới để xử lý khi người dùng chủ động đóng/hủy từ WebView
                        Log.d("VnPayOnlyScreen_WebView", "WebView closed or cancelled by user.")
                        showWebViewState = false // Ẩn WebView
                        // Chỉ reset nếu trạng thái hiện tại chưa phải là kết quả cuối cùng (success/failed/cancelled từ VNPay)
                        if (viewModel.vnPayPaymentState.value is VnPayPaymentState.UrlGenerated ||
                            viewModel.vnPayPaymentState.value is VnPayPaymentState.GeneratingUrl ||
                            viewModel.vnPayPaymentState.value is VnPayPaymentState.Idle) {
                            // Giả sử người dùng hủy khi nhấn back từ cổng VNPay
                            viewModel.handleWebViewCancellation()
                        }
                        // Nếu không, không làm gì cả vì handleVnPayReturn đã được gọi qua shouldOverrideUrlLoading
                    }
                )
            }
        } // End Box
    } // End Scaffold
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VnPayWebViewInScreen(
    url: String,
    isVisible: Boolean, // Thêm tham số này
    onShouldOverrideUrlLoading: (view: WebView, url: String?) -> Boolean,
    onWebViewClosedOrCancelled: () -> Unit // Callback mới
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Chỉ tạo WebView nếu isVisible = true
    if (isVisible) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                            return onShouldOverrideUrlLoading(view, url)
                        }
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            Log.d("VnPayWebViewInScreen", "Page Started: $url")
                        }
                        override fun onPageFinished(view: WebView, url: String?) {
                            super.onPageFinished(view, url)
                            Log.d("VnPayWebViewInScreen", "Page Finished: $url")
                        }
                    }
                    webChromeClient = WebChromeClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    webViewInstance = this
                    Log.d("VnPayWebViewInScreen", "Factory: Loading URL: $url")
                    loadUrl(url)
                }
            },
            update = { view ->
                webViewInstance = view // Cập nhật instance
                // Chỉ load lại URL nếu URL truyền vào thực sự thay đổi VÀ WebView đang hiển thị
                // và URL hiện tại của WebView khác với URL mới (để tránh vòng lặp vô hạn)
                if (view.url != url) {
                    Log.d("VnPayWebViewInScreen", "Update: Loading new URL: $url (current: ${view.url})")
                    view.loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    // BackHandler này sẽ được kích hoạt khi isVisible (tức là WebView đang được hiển thị)
    BackHandler(enabled = isVisible) {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            // Người dùng nhấn back khi không thể back trong WebView nữa -> coi như đóng/hủy
            onWebViewClosedOrCancelled()
        }
    }
}

@Composable
fun PaymentResultCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    message: String,
    details: List<String>,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp) // Khoảng cách giữa các item
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = iconColor, // Dùng màu của icon cho tiêu đề
                textAlign = TextAlign.Center
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            details.forEach { detail ->
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(0.8f), // Nút chiếm 80% chiều rộng Card
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (iconColor == MaterialTheme.colorScheme.error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(buttonText)
            }
        }
    }
}