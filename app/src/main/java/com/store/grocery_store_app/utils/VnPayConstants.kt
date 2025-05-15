package com.store.grocery_store_app.utils

object VnPayConstants {
    // THÔNG TIN SANDBOX TỪ EMAIL CỦA BẠN
    const val TMN_CODE = "DL5UJPV6"
    // CẢNH BÁO: Đặt HASH_SECRET ở đây là RẤT NGUY HIỂM cho production
    // Giả sử không có khoảng trắng trong key. Nếu có, bạn cần kiểm tra lại.
    const val HASH_SECRET = "DBMJHQCNBTNZVQ5ZHU8KVE6HJ4V9POQO"
    const val PAYMENT_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"
    const val VERSION = "2.1.0" // Phiên bản API của VNPay

    // Deep link của ứng dụng bạn để VNPay gọi lại
    // Bạn cần định nghĩa scheme và host này trong AndroidManifest.xml
    const val RETURN_URL_SCHEME = "yourvnpayapp" // Ví dụ: đổi thành tên app của bạn
    const val RETURN_URL_HOST = "vnpaymentreturn"
    fun getReturnUrl(): String = "$RETURN_URL_SCHEME://$RETURN_URL_HOST"
}