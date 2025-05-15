package com.store.grocery_store_app.ui.screens.vnpay

// src/main/java/com/yourpackage/vnpayonly/VnPayOnlyViewModel.kt

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
// import androidx.compose.runtime.mutableStateOf // Không cần nếu dùng StateFlow cho UI state
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.utils.VnPayConstants
import com.store.grocery_store_app.utils.VnPayUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

sealed class VnPayPaymentState {
    object Idle : VnPayPaymentState()
    object GeneratingUrl : VnPayPaymentState()
    data class UrlGenerated(val paymentUrl: String, val appTxnRef: String) : VnPayPaymentState()
    data class ProcessingReturn(val message: String) : VnPayPaymentState()
    data class PaymentSuccess(val message: String, val txnRef: String) : VnPayPaymentState()
    data class PaymentFailed(val message: String, val txnRef: String?, val responseCode: String?) : VnPayPaymentState()
    data class Error(val message: String) : VnPayPaymentState()
    data class PaymentCancelled(val message: String) : VnPayPaymentState() // Đã thêm
}

@HiltViewModel
class VnPayOnlyViewModel @Inject constructor() : ViewModel() {

    private val _vnPayPaymentStateFlow = MutableStateFlow<VnPayPaymentState>(VnPayPaymentState.Idle)
    val vnPayPaymentState = _vnPayPaymentStateFlow.asStateFlow()

    var currentTxnRef: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun initiateVnPayPayment(amount: Long, orderDescription: String) {
        viewModelScope.launch {
            _vnPayPaymentStateFlow.value = VnPayPaymentState.GeneratingUrl
            try {
                val txnRef = generateLocalTxnRef()
                currentTxnRef = txnRef

                Log.d("VnPayOnlyVM", "Initiating VNPay. Amount: $amount, Desc: $orderDescription, TxnRef: $txnRef")

                val paymentUrl = VnPayUtils.buildPaymentUrl(
                    amount = amount,
                    orderInfo = orderDescription,
                    txnRef = txnRef
                )
                _vnPayPaymentStateFlow.value = VnPayPaymentState.UrlGenerated(paymentUrl, txnRef)

            } catch (e: Exception) {
                Log.e("VnPayOnlyVM", "Error generating VNPay URL: ${e.message}", e)
                _vnPayPaymentStateFlow.value = VnPayPaymentState.Error("Lỗi tạo URL VNPay: ${e.message}")
            }
        }
    }

    fun handleVnPayReturn(returnData: Map<String, String?>) {
        _vnPayPaymentStateFlow.value = VnPayPaymentState.ProcessingReturn("Đang xử lý kết quả VNPay...")
        Log.d("VnPayOnlyVM", "Handling VNPay Return Data: $returnData")

        val vnp_TxnRef = returnData["vnp_TxnRef"]
        val vnp_ResponseCode = returnData["vnp_ResponseCode"]

        if (vnp_TxnRef != currentTxnRef) {
            Log.e("VnPayOnlyVM", "TxnRef mismatch! Expected: $currentTxnRef, Got: $vnp_TxnRef")
            _vnPayPaymentStateFlow.value = VnPayPaymentState.PaymentFailed("Lỗi: Mã giao dịch không khớp.", vnp_TxnRef, vnp_ResponseCode)
            return
        }

        // Kiểm tra mã hủy đặc biệt từ VNPay (ví dụ: '24' là User cancel)
        // Mã này có thể thay đổi, cần xem tài liệu VNPay để biết chính xác mã hủy
        if (vnp_ResponseCode == "24") { // Mã 24: Khách hàng hủy giao dịch.
            Log.w("VnPayOnlyVM", "Payment Cancelled by User on VNPay Gateway. TxnRef: $vnp_TxnRef, ResponseCode: $vnp_ResponseCode")
            _vnPayPaymentStateFlow.value = VnPayPaymentState.PaymentCancelled("Người dùng đã hủy giao dịch trên cổng VNPay.")
            return
        }

        val isSignatureValid = VnPayUtils.verifyReturnUrlSignature(returnData, VnPayConstants.HASH_SECRET)
        if (!isSignatureValid) {
            Log.e("VnPayOnlyVM", "VNPay Return Signature INVALID!")
            _vnPayPaymentStateFlow.value = VnPayPaymentState.PaymentFailed("Lỗi: Chữ ký VNPay không hợp lệ.", vnp_TxnRef, vnp_ResponseCode)
            return
        }
        Log.i("VnPayOnlyVM", "VNPay Return Signature VERIFIED.")

        if (vnp_ResponseCode == "00") {
            val transactionStatus = returnData["vnp_TransactionStatus"]
            if (transactionStatus == null || transactionStatus == "00") {
                Log.i("VnPayOnlyVM", "Payment Success. TxnRef: $vnp_TxnRef")
                _vnPayPaymentStateFlow.value = VnPayPaymentState.PaymentSuccess("Thanh toán VNPay thành công cho đơn hàng $vnp_TxnRef.", vnp_TxnRef ?: "N/A")
            } else {
                Log.w("VnPayOnlyVM", "Payment Potentially Failed (TransactionStatus not 00). TxnRef: $vnp_TxnRef, ResponseCode: $vnp_ResponseCode, TransactionStatus: $transactionStatus")
                _vnPayPaymentStateFlow.value = VnPayPaymentState.PaymentFailed("Thanh toán VNPay chưa hoàn tất. Trạng thái GD: $transactionStatus.", vnp_TxnRef, vnp_ResponseCode)
            }
        } else {
            Log.w("VnPayOnlyVM", "Payment Failed with ResponseCode: $vnp_ResponseCode. TxnRef: $vnp_TxnRef")
            // Tra cứu ý nghĩa mã lỗi vnp_ResponseCode trong tài liệu VNPay
            _vnPayPaymentStateFlow.value = VnPayPaymentState.PaymentFailed("Thanh toán VNPay thất bại. Mã lỗi VNPay: $vnp_ResponseCode.", vnp_TxnRef, vnp_ResponseCode)
        }
    }

    fun generateLocalTxnRef(): String {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        return sdf.format(Date()) + "_" + UUID.randomUUID().toString().substring(0, 6)
    }

    fun resetPaymentState() {
        _vnPayPaymentStateFlow.value = VnPayPaymentState.Idle
        currentTxnRef = null
    }
    fun handleWebViewCancellation() {
        Log.d("VnPayOnlyVM", "WebView cancellation initiated by user back press.")
        // Khi người dùng back từ WebView trước khi VNPay redirect,
        // chúng ta coi như là hủy giao dịch.
        // Nếu currentTxnRef đã được tạo (tức là đã qua bước GeneratingUrl),
        // chúng ta có thể gắn nó vào thông báo lỗi/hủy.
        // VNPay có thể không có vnp_TxnRef trong trường hợp này.
        // Mã "24" là mã hủy của VNPay trên return URL.
        // Ở đây, chúng ta tự tạo ra trạng thái hủy.
        _vnPayPaymentStateFlow.value = VnPayPaymentState.PaymentCancelled(
            "Người dùng đã hủy thanh toán bằng cách thoát khỏi WebView."
        )
        // Không cần truyền TxnRef ở đây vì nó không phải là callback từ VNPay
        // Hoặc bạn có thể chọn cách xử lý khác:
        // resetPaymentState() // Nếu muốn đưa về trạng thái Idle ngay
    }
}