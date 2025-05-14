import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
<<<<<<< HEAD
import com.store.grocery_store_app.data.models.response.AddressDTO
=======
import com.store.grocery_store_app.data.models.DeliveryDetail
>>>>>>> remotes/origin/main
import com.store.grocery_store_app.data.models.response.VoucherResponse
import com.store.grocery_store_app.ui.navigation.Screen
import com.store.grocery_store_app.ui.screens.EmailVerification.EmailVerificationScreen
import com.store.grocery_store_app.ui.screens.account.AccountScreen
import com.store.grocery_store_app.ui.screens.address.Address
import com.store.grocery_store_app.ui.screens.address.AddressListScreen
import com.store.grocery_store_app.ui.screens.address.EditAddressScreen
import com.store.grocery_store_app.ui.screens.auth.AuthViewModel
import com.store.grocery_store_app.ui.screens.cart.CartScreen
import com.store.grocery_store_app.ui.screens.category.CategoryScreen
import com.store.grocery_store_app.ui.screens.checkout.CheckoutScreen
import com.store.grocery_store_app.ui.screens.checkout.Product
import com.store.grocery_store_app.ui.screens.home.HomeScreen
import com.store.grocery_store_app.ui.screens.intro.IntroScreen
import com.store.grocery_store_app.ui.screens.login.LoginScreen
import com.store.grocery_store_app.ui.screens.order.OrderScreen
import com.store.grocery_store_app.ui.screens.otp.OtpVerificationScreen
import com.store.grocery_store_app.ui.screens.ProductDetails.ProductDetailsScreen
import com.store.grocery_store_app.ui.screens.ProductsByCategory.ProductsByCategoryScreen
import com.store.grocery_store_app.ui.screens.address.AddAddressScreen
import com.store.grocery_store_app.ui.screens.address.AddressScreen
import com.store.grocery_store_app.ui.screens.register.RegisterScreen
import com.store.grocery_store_app.ui.screens.forgotpassword.ResetPasswordScreen
import com.store.grocery_store_app.ui.screens.order.DeliveryDetailScreen
import com.store.grocery_store_app.ui.screens.profile.ProfileScreen
import com.store.grocery_store_app.ui.screens.reviews.ReviewProductScreen
import com.store.grocery_store_app.ui.screens.search.SearchScreen
import com.store.grocery_store_app.ui.screens.splash.SplashScreen
import com.store.grocery_store_app.ui.screens.voucher.VoucherScreen
import com.store.grocery_store_app.utils.AuthPurpose
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import com.store.grocery_store_app.ui.screens.address.SelectAddressScreen

/**
 * AuthNavGraph kết hợp logic của cả hai nhánh mà không còn conflict.
 * Các màn hình mới (Account, Address, CheckOut, Voucher, v.v.) đã được hợp nhất.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        /* -------------------- Auth & On‑boarding -------------------- */

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.EmailVerification.createRoute(AuthPurpose.REGISTRATION))
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.EmailVerification.createRoute(AuthPurpose.PASSWORD_RESET))
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Email Verification Screen
        composable(
            route = Screen.EmailVerification.route,
            arguments = listOf(navArgument("purpose") { type = NavType.StringType })
        ) { backStackEntry ->
            val purpose = backStackEntry.arguments?.getString("purpose")?.let {
                AuthPurpose.valueOf(it)
            } ?: AuthPurpose.REGISTRATION

            EmailVerificationScreen(
                purpose = purpose,
                onNavigateToOtp = { email ->
                    navController.navigate(Screen.OtpVerification.createRoute(purpose, email))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // OTP Verification Screen
        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(
                navArgument("purpose") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val purpose = backStackEntry.arguments?.getString("purpose")?.let {
                AuthPurpose.valueOf(it)
            } ?: AuthPurpose.REGISTRATION
            val email = backStackEntry.arguments?.getString("email") ?: ""

            OtpVerificationScreen(
                purpose = purpose,
                email = email,
                onVerificationSuccess = {
                    when (purpose) {
                        AuthPurpose.REGISTRATION   -> navController.navigate(Screen.Register.createRoute(email))
                        AuthPurpose.PASSWORD_RESET -> navController.navigate(Screen.ResetPassword.createRoute(email))
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Register Screen
        composable(
            route = Screen.Register.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterScreen(
                email = email,
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Reset Password Screen
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(
                email = email,
                onResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onIntro = {
                    navController.navigate(Screen.Intro.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                }
            )
        }

        // Intro Screen
        composable(Screen.Intro.route) {
            IntroScreen(
                authViewModel,
                onAutoLogin = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        /* -------------------- Main Screens -------------------- */

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProductDetails = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateToOrder = { navController.navigate(Screen.Order.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToCategory = { navController.navigate(Screen.Category.route) },
                onNavigateToNotification = { /* TODO */ },
                onNavigateToAccount = { navController.navigate(Screen.Account.route) },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        // Category Screen
        composable(Screen.Category.route) {
            CategoryScreen(
                onNavigateToProductsByCategory = { categoryId ->
                    navController.navigate(Screen.ProductsByCategory.createRoute(categoryId))
                },
                onNavigateToOrder = { navController.navigate(Screen.Order.route) },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onNavigateToNotification = { /* TODO */ },
                onNavigateToAccount = { /* TODO */ }
            )
        }

        // Products by Category Screen
        composable(
            route = Screen.ProductsByCategory.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L

            ProductsByCategoryScreen(
                categoryId = categoryId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProductDetails = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                }
            )
        }

        // Product Details Screen
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L

            ProductDetailsScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onAddToCartSuccess = { navController.popBackStack() },
                onNavigateToProduct = { newProductId ->
                    navController.navigate(Screen.ProductDetails.createRoute(newProductId)) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route) {
                        popUpTo(Screen.Cart.route) { inclusive = true }
                    }
                }
            )
        }

        // Unified Search Screen
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                }
            )
        }

        // Order Screen
        composable(Screen.Order.route) {
            OrderScreen(
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToReviewProduct = { orderId, orderItemId ->
                    navController.navigate(Screen.Review.createRoute(orderId, orderItemId))
                },
                onNavigateToProductDetails = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateToDeliveryDetail = { deliveryDetailId ->
                    navController.navigate(Screen.DeliveryDetail.createRoute(deliveryDetailId))
                },
            )
        }

        // Review Product Screen
        composable(
            route = Screen.Review.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.LongType },
                navArgument("orderItemId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val orderId     = backStackEntry.arguments?.getLong("orderId")     ?: 0L
            val orderItemId = backStackEntry.arguments?.getLong("orderItemId") ?: 0L

            ReviewProductScreen(
                orderId = orderId,
                orderItemId = orderItemId,
                onNavigateToOrder = { navController.popBackStack() }
            )
        }

        /* -------------------- Cart & Check‑out flow -------------------- */

        // Cart Screen
        composable(Screen.Cart.route) {
            CartScreen(
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onPayment = { selectedProducts ->
                    val json = Uri.encode(Gson().toJson(selectedProducts))
                    val selectedVoucherJson = Uri.encode(Gson().toJson(""))  // Chưa chọn voucher, gửi chuỗi rỗng
                    navController.navigate(Screen.CheckOut.createRoute(json, selectedVoucherJson))
                },
                onNavigateVoucher = {
                    navController.navigate(Screen.Voucher.route) {
                        popUpTo(Screen.Voucher.route) { inclusive = true }
                    }
                }
            )
        }

        // Sửa lại phần CheckOut composable trong NavGraph
        composable(
            route = Screen.CheckOut.route,
            arguments = listOf(
                navArgument("selectedProductsJson") { type = NavType.StringType },
                navArgument("selectedVoucherJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Lấy chuỗi JSON từ tham số route
            val selectedProductsJson = backStackEntry.arguments?.getString("selectedProductsJson") ?: "[]"
            val selectedVoucherJson = backStackEntry.arguments?.getString("selectedVoucherJson") ?: ""

            // Chuyển đổi chuỗi JSON thành danh sách sản phẩm
            val selectedProducts = try {
                Gson().fromJson(selectedProductsJson, Array<Product>::class.java).toList()
            } catch (e: Exception) {
                Log.e("CheckOut", "Error parsing products JSON: ${e.message}")
                emptyList()
            }

            // Lấy voucher từ saved state handle
            val selectedVoucherFromHandle: VoucherResponse? = backStackEntry.savedStateHandle.get<VoucherResponse>("selectedVoucher")

            // Lấy address từ saved state handle
            val selectedAddressFromHandle: AddressDTO? = backStackEntry.savedStateHandle.get<AddressDTO>("selectedAddress")

            CheckoutScreen(
                products = selectedProducts,
                voucher = selectedVoucherFromHandle,
                selectedAddress = selectedAddressFromHandle,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateAddress = {
                    navController.navigate(
                        Screen.SelectAddress.createRoute(selectedAddressFromHandle?.id)
                    )
                },
                onNavigateVoucher = {
                    navController.navigate(Screen.Voucher.route) {
                        popUpTo(Screen.Voucher.route) { inclusive = true }
                    }
                },
                /*onNavigateAddAddress = {
                    navController.navigate(Screen.AddAddress.route)
                }*/
            )
        }

        /* -------------------- Address flow -------------------- */

        // Address List
        composable(Screen.Address.route) {
            AddressScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddAddress = {
                   navController.navigate(Screen.AddAddress.route)
                },
                onNavigateToEditAddress = { addressId ->
                    navController.navigate(Screen.EditAddress.createRoute(addressId))
                }
            )
        }
        // Add Address Screen
        composable(Screen.AddAddress.route) {
            AddAddressScreen(
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // Edit Address Screen
        composable(
            route = Screen.EditAddress.route,
            arguments = listOf(navArgument("addressId") { type = NavType.LongType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getLong("addressId") ?: 0L

            EditAddressScreen(
                addressId = addressId,
                onNavigateBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SelectAddress.route,
            arguments = listOf(
                navArgument("selectedAddressId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val selectedAddressId = backStackEntry.arguments?.getLong("selectedAddressId", -1L)
                ?.takeIf { it != -1L }

            SelectAddressScreen(
                selectedAddressId = selectedAddressId,
                onSelectAddress = { address ->
                    // Trả địa chỉ đã chọn về CheckOut screen
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedAddress", address)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        /* -------------------- Voucher -------------------- */
        composable(Screen.Voucher.route) {
            VoucherScreen(
                onConfirm = { voucher ->
                    Log.d("Voucher", voucher.toString())
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedVoucher", voucher)
                    navController.popBackStack()
                } ,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        /* -------------------- Account -------------------- */
        composable(Screen.Account.route) {
            AccountScreen(
                onNavigateToHome = {
                    if (navController.currentDestination?.route != Screen.Home.route) {
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                    }
                },
                onNavigateToCategory = { navController.navigate(Screen.Category.route) },
                onNavigateToNotification = { /* TODO */ },
                onNavigateToOrder = { navController.navigate(Screen.Order.route) } ,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAddress = {
                    navController.navigate(Screen.Address.route)
                }
            )
        }

        // Profile Screen
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.DeliveryDetail.route,
            arguments = listOf(navArgument("deliveryDetailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedOrder = backStackEntry.arguments?.getString("deliveryDetailId") ?: ""
            DeliveryDetailScreen(
                deliveryOrder = selectedOrder,
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}


