// src/main/java/com/yourpackage/utils/VnPayUtils.kt
package com.store.grocery_store_app.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.store.grocery_store_app.utils.VnPayConstants
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object VnPayUtils {

    private const val HMAC_SHA512_ALGORITHM = "HmacSHA512"

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun hmacSHA512(key: String, data: String): String {
        val mac = Mac.getInstance(HMAC_SHA512_ALGORITHM)
        val secretKeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), HMAC_SHA512_ALGORITHM)
        mac.init(secretKeySpec)
        val hash = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buildPaymentUrl(
        amount: Long,
        orderInfo: String,
        txnRef: String,
        ipAddress: String = "127.0.0.1"
    ): String {
        val vnpParams = TreeMap<String, String>()
        // ... (Thêm các params vnp_Version đến vnp_IpAddr như cũ) ...
        vnpParams["vnp_Version"] = VnPayConstants.VERSION
        vnpParams["vnp_Command"] = "pay"
        vnpParams["vnp_TmnCode"] = VnPayConstants.TMN_CODE
        vnpParams["vnp_Amount"] = (amount * 100).toString()
        vnpParams["vnp_CurrCode"] = "VND"
        vnpParams["vnp_TxnRef"] = txnRef
        vnpParams["vnp_OrderInfo"] = orderInfo // Giá trị gốc
        vnpParams["vnp_OrderType"] = "other"
        vnpParams["vnp_Locale"] = "vn"
        vnpParams["vnp_ReturnUrl"] = VnPayConstants.getReturnUrl() // Giá trị gốc
        vnpParams["vnp_IpAddr"] = ipAddress

        val zoneIdGmt7 = ZoneId.of("Asia/Ho_Chi_Minh") // Hoặc "Etc/GMT+7"
        val currentGmt7Time = ZonedDateTime.now(zoneIdGmt7)
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

        val vnpCreateDate = currentGmt7Time.format(formatter)
        vnpParams["vnp_CreateDate"] = vnpCreateDate // Giá trị gốc (chuỗi đã format)

        val expireGmt7Time = currentGmt7Time.plusMinutes(15)
        val vnpExpireDate = expireGmt7Time.format(formatter)
        vnpParams["vnp_ExpireDate"] = vnpExpireDate // Giá trị gốc (chuỗi đã format)


        // === THAY ĐỔI QUAN TRỌNG: CÁCH TẠO HASHDATASTRING ===
        // Dựa theo code mẫu JAVA của VNPay: URL Encode giá trị TRƯỚC KHI hash
        val hashDataList = mutableListOf<String>()
        for ((key, value) in vnpParams) {
            if (value.isNotEmpty()) {
                // THEO CODE MẪU JAVA CỦA VNPAY, HỌ DÙNG US_ASCII ĐỂ ENCODE TRONG HASH
                // NHƯNG UTF-8 PHỔ BIẾN HƠN CHO URL. NẾU UTF-8 VẪN LỖI, THỬ US_ASCII
                hashDataList.add("$key=${URLEncoder.encode(value, StandardCharsets.UTF_8.name())}")
            }
        }
        val hashDataString = hashDataList.joinToString("&")
        Log.d("VnPayUtils", "HashDataString for MAC (Values URL Encoded before hashing): $hashDataString")
        // === KẾT THÚC THAY ĐỔI QUAN TRỌNG ===


        val secureHash = try {
            hmacSHA512(VnPayConstants.HASH_SECRET, hashDataString)
        } catch (e: Exception) {
            Log.e("VnPayUtils", "Error generating HMAC: ${e.message}")
            return "ERROR_CREATING_HASH"
        }
        Log.d("VnPayUtils", "Generated vnp_SecureHash (Values URL Encoded): $secureHash")


        // Build query string cuối cùng cho URL
        // Ở bước này, các giá trị đã được encode khi tạo hashDataList
        // nên chúng ta chỉ cần ghép lại và thêm secureHash đã encode.
        // Tuy nhiên, để đảm bảo key cũng được encode và secureHash cũng được encode, làm lại:
        val finalQueryList = mutableListOf<String>()
        for ((key, value) in vnpParams) { // Lặp lại qua vnpParams gốc
            if (value.isNotEmpty()) {
                finalQueryList.add(
                    "${URLEncoder.encode(key, StandardCharsets.UTF_8.name())}=" +
                            URLEncoder.encode(value, StandardCharsets.UTF_8.name()) // Encode giá trị gốc
                )
            }
        }
        finalQueryList.add(
            "${URLEncoder.encode("vnp_SecureHash", StandardCharsets.UTF_8.name())}=" +
                    URLEncoder.encode(secureHash, StandardCharsets.UTF_8.name())
        )

        val paymentUrl = "${VnPayConstants.PAYMENT_URL}?${finalQueryList.joinToString("&")}"
        Log.d("VnPayUtils", "vnp_CreateDate sent: $vnpCreateDate")
        Log.d("VnPayUtils", "vnp_ExpireDate sent: $vnpExpireDate")
        Log.d("VnPayUtils", "Final Payment URL (Values Encoded in Hash): $paymentUrl")

        return paymentUrl
    }

    fun verifyReturnUrlSignature(
        responseData: Map<String, String?>, // query parameters từ return URL (đã được URL decode tự động)
        hashSecret: String
    ): Boolean {
        val receivedSecureHash = responseData["vnp_SecureHash"] ?: run {
            Log.e("VnPayUtilsVerify", "vnp_SecureHash is missing in response data from VNPay.")
            return false
        }

        val paramsToHash = TreeMap<String, String>() // TreeMap để sắp xếp key
        responseData.forEach { (key, originalDecodedValue) ->
            // Chỉ lấy các tham số khác vnp_SecureHash và vnp_SecureHashType (nếu có) và có giá trị
            if (key != "vnp_SecureHash" && key != "vnp_SecureHashType" && originalDecodedValue != null) {
                paramsToHash[key] = originalDecodedValue
            }
        }

        // TẠO CHUỖI HASH CHO DỮ LIỆU TRẢ VỀ: CÁC GIÁ TRỊ PHẢI ĐƯỢC URL ENCODE LẠI
        // GIỐNG NHƯ CÁCH ĐÃ LÀM KHI TẠO HASH CHO YÊU CẦU GỬI ĐI
        val hashDataListForVerification = mutableListOf<String>()
        for ((key, originalDecodedValue) in paramsToHash) {
            // Giá trị originalDecodedValue đã được Android decode từ URL.
            // Giờ chúng ta encode lại nó trước khi đưa vào chuỗi hash để xác thực.
            if (originalDecodedValue.isNotEmpty()) { // Vẫn kiểm tra isNotEmpty cho chắc
                try {
                    // Dùng UTF-8. Nếu vẫn sai, hãy cân nhắc thử US_ASCII như code Java mẫu của VNPay cho IPN.
                    val reEncodedValue = URLEncoder.encode(originalDecodedValue, StandardCharsets.UTF_8.name())
                    hashDataListForVerification.add("$key=$reEncodedValue")
                    Log.v("VnPayUtilsVerifyDetail", "Return Hash Param: $key=$reEncodedValue (Original Decoded: $originalDecodedValue)")
                } catch (e: Exception) {
                    Log.e("VnPayUtilsVerify", "Error URL re-encoding value for return signature: '$originalDecodedValue' for key '$key'", e)
                    return false // Nếu không thể encode, chữ ký sẽ không bao giờ khớp
                }
            }
        }
        val dataToHashForVerification = hashDataListForVerification.joinToString("&")
        Log.d("VnPayUtilsVerify", "Return DataToHash for MAC (Values Re-Encoded): $dataToHashForVerification")

        val calculatedHash = try {
            hmacSHA512(hashSecret, dataToHashForVerification)
        } catch (e: Exception) {
            Log.e("VnPayUtilsVerify", "Error generating HMAC for return verification: ${e.message}")
            return false
        }

        Log.d("VnPayUtilsVerify", "Return CalculatedHash (Values Re-Encoded): $calculatedHash")
        Log.d("VnPayUtilsVerify", "Return ReceivedHash from VNPay: $receivedSecureHash")

        val signatureMatch = calculatedHash.equals(receivedSecureHash, ignoreCase = true)
        if (!signatureMatch) {
            Log.e("VnPayUtilsVerify", "!!! VNPAY RETURN SIGNATURE MISMATCH !!!")
            Log.e("VnPayUtilsVerify", "Data Used for Calc: $dataToHashForVerification")
            Log.e("VnPayUtilsVerify", "Calculated Hash   : $calculatedHash")
            Log.e("VnPayUtilsVerify", "Received Hash     : $receivedSecureHash")
        } else {
            Log.i("VnPayUtilsVerify", "VNPAY RETURN SIGNATURE VERIFIED SUCCESSFULLY!")
        }
        return signatureMatch
    }
}