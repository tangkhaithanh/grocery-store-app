package com.store.grocery_store_app.ui.screens.intro

import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.R
import com.store.grocery_store_app.ui.screens.auth.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onAutoLogin: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    // Delay để tạo hiệu ứng mượt
    LaunchedEffect(Unit) {
        delay(300) // thời gian chờ ngắn cho hiệu ứng tự nhiên
        visible = true
        delay(600)

        //Check logined
        if (authViewModel.authState.value.isLoggedIn) {
            onAutoLogin()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_grocery_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(160.dp)
                )

                // Animated slogan
                AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        text = "Mua sắm dễ dàng,\nmọi lúc mọi nơi!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Button section
                AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Guest button
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Tiếp tục với tư cách Khách", fontSize = 16.sp)
                        }

                        // User button
                        OutlinedButton(
                            onClick = {
                                onNavigateLogin()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                        ) {
                            Text("Đăng nhập bằng tài khoản", fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}