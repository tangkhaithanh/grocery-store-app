
package com.store.grocery_store_app.ui.navigation

import com.google.gson.Gson
import com.store.grocery_store_app.ui.screens.checkout.Product
import com.store.grocery_store_app.utils.AuthPurpose
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Unified Screen sealed class after resolving merge conflicts.
 * Contains all app destinations with helper createRoute functions.
 */
sealed class Screen(val route: String) {

    /* -------------------- Auth flow -------------------- */

    object Login : Screen("login")

    object EmailVerification : Screen("email_verification/{purpose}") {
        fun createRoute(purpose: AuthPurpose) = "email_verification/${purpose.name}"
    }

    object OtpVerification : Screen("otp_verification/{purpose}/{email}") {
        fun createRoute(purpose: AuthPurpose, email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "otp_verification/${purpose.name}/$encodedEmail"
        }
    }

    object Register : Screen("register/{email}") {
        fun createRoute(email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "register/$encodedEmail"
        }
    }

    object ResetPassword : Screen("reset_password/{email}") {
        fun createRoute(email: String): String {
            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.toString())
            return "reset_password/$encodedEmail"
        }
    }

    /* -------------------- Main tabs -------------------- */

    object Home    : Screen("home")
    object Order   : Screen("order")
    object Splash  : Screen("splash")
    object Intro   : Screen("intro")

    /* -------------------- Product & category -------------------- */

    object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: Long): String = "product_details/$productId"
    }

    object Review : Screen("review/{orderId}/{orderItemId}") {
        fun createRoute(orderId: Long, orderItemId: Long): String = "review/$orderId/$orderItemId"
    }

    object Search  : Screen("search")
    object Cart    : Screen("cart")
    object Category: Screen("category")

    object ProductsByCategory : Screen("products_by_category/{categoryId}") {
        fun createRoute(categoryId: Long): String = "products_by_category/$categoryId"
    }

    /* -------------------- Check-out flow -------------------- */

    object CheckOut : Screen("checkout/{selectedProductsJson}/{selectedVoucherJson}") {

        /**
         * Pass a JSON string (already encoded/escaped if necessary).
         */
        fun createRoute(selectedProductsJson: String, selectedVoucherJson: String): String {
            return "checkout/$selectedProductsJson/$selectedVoucherJson"
        }
    }

    /* -------------------- Address flow -------------------- */

    object Address : Screen("address")

    object AddAddress : Screen("add_address")

    object SelectAddress : Screen("select_address/{selectedAddressId}") {
        fun createRoute(selectedAddressId: Long?): String {
            return "select_address/${selectedAddressId ?: -1}"
        }
    }

    object EditAddress : Screen("edit_address/{addressId}") {
        fun createRoute(addressId: Long): String = "edit_address/$addressId"
    }

    /* -------------------- Voucher -------------------- */

    object Voucher : Screen("voucher")

    /* -------------------- Account -------------------- */

    object Account : Screen("account")

    object Profile : Screen("profile")

    /* -------------------- Delivery Detail -------------------- */
    object DeliveryDetail: Screen("deliveryDetail/{deliveryDetailId}") {
        fun createRoute(deliveryDetailId: String): String {
            return "deliveryDetail/$deliveryDetailId"
        }
    }

    object VnPay : Screen("vnpay/{totalAmount}/{productJson}/{addressJson}/{voucherJson}") {
        fun createRoute(totalAmount: Long, productJson: String, addressJson: String, voucherJson: String): String {
            return "vnpay/$totalAmount/$productJson/$addressJson/$voucherJson"
        }
    }
}
