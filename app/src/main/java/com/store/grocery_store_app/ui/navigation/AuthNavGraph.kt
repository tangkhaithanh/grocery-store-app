import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.store.grocery_store_app.ui.screens.EmailVerification.EmailVerificationScreen
import com.store.grocery_store_app.ui.screens.login.LoginScreen
import com.store.grocery_store_app.ui.screens.otp.OtpVerificationScreen
import com.store.grocery_store_app.ui.screens.register.RegisterScreen
import com.store.grocery_store_app.ui.screens.forgotpassword.ResetPasswordScreen
import com.store.grocery_store_app.ui.screens.auth.AuthViewModel
import com.store.grocery_store_app.ui.navigation.Screen
import com.store.grocery_store_app.ui.screens.ProductDetails.ProductDetailsScreen
import com.store.grocery_store_app.ui.screens.home.HomeScreen
import com.store.grocery_store_app.ui.screens.intro.IntroScreen
import com.store.grocery_store_app.ui.screens.order.OrderScreen
import com.store.grocery_store_app.ui.screens.splash.SplashScreen
import com.store.grocery_store_app.utils.AuthPurpose

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
        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(
                        Screen.EmailVerification.createRoute(AuthPurpose.REGISTRATION)
                    )
                },
                onNavigateToForgotPassword = {
                    navController.navigate(
                        Screen.EmailVerification.createRoute(AuthPurpose.PASSWORD_RESET)
                    )
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
            arguments = listOf(
                navArgument("purpose") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val purpose = backStackEntry.arguments?.getString("purpose")?.let {
                AuthPurpose.valueOf(it)
            } ?: AuthPurpose.REGISTRATION

            EmailVerificationScreen(
                purpose = purpose,
                onNavigateToOtp = { email ->
                    navController.navigate(
                        Screen.OtpVerification.createRoute(purpose, email)
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // OTP Verification Screen
        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(
                navArgument("purpose") {
                    type = NavType.StringType
                },
                navArgument("email") {
                    type = NavType.StringType
                }
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
                        AuthPurpose.REGISTRATION -> {
                            navController.navigate(
                                Screen.Register.createRoute(email)
                            )
                        }
                        AuthPurpose.PASSWORD_RESET -> {
                            navController.navigate(
                                Screen.ResetPassword.createRoute(email)
                            )
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Register Screen
        composable(
            route = Screen.Register.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            RegisterScreen(
                email = email,
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Reset Password Screen
        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            ResetPasswordScreen(
                email = email,
                onResetSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Home Screen
        composable(route = Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProductDetails = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateToOrder = {
                    navController.navigate(Screen.Order.route)
                }
            )
        }

        composable(route = Screen.Order.route) {
            OrderScreen(
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true}
                    }
                }
            )
        }

<<<<<<< HEAD
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onIntro = {
                    navController.navigate(Screen.Intro.route) {
                        popUpTo(Screen.Intro.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Intro.route) {
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
=======

        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L

            ProductDetailsScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddToCartSuccess = {
                    // Show a snackbar or toast for successful cart addition
                    // For now, just go back to the previous screen
                    navController.popBackStack()
                },
                onNavigateToProduct = { newProductId ->
                    // Điều hướng đến trang chi tiết sản phẩm mới
                    // Thay vì sử dụng popBackStack, chúng ta điều hướng đến sản phẩm mới

                    // Cách 1: Sử dụng navigate và thay thế current destination
                    navController.navigate(Screen.ProductDetails.createRoute(newProductId)) {
                        // Xóa destination hiện tại khỏi back stack để tránh việc stack quá nhiều
                        // khi người dùng liên tục chọn sản phẩm tương tự
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            // Xóa màn hình chi tiết sản phẩm hiện tại
                            inclusive = true
                        }
                    }

                    // Cách 2 (thay thế): Pop và push
                    // navController.popBackStack()
                    // navController.navigate(Screen.ProductDetails.createRoute(newProductId))
>>>>>>> remotes/origin/dev_thanh
                }
            )
        }
    }
}