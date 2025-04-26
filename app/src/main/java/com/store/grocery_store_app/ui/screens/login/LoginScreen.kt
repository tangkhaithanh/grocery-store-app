package com.store.grocery_store_app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.CustomButton
import com.store.grocery_store_app.ui.components.CustomTextField
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.ui.screens.auth.LoginViewModel
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Effect to handle successful login
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App logo or title
            Text(
                text = "Grocery Store",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Login form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Đăng nhập",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Email field
                    CustomTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email",
                        placeholder = "Nhập email của bạn",
                        leadingIcon = Icons.Default.Email,
                        isError = state.emailError.isNotEmpty(),
                        errorMessage = state.emailError,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Password field
                    CustomTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Mật khẩu",
                        placeholder = "Nhập mật khẩu của bạn",
                        leadingIcon = Icons.Default.Lock,
                        isError = state.passwordError.isNotEmpty(),
                        errorMessage = state.passwordError,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        isPassword = true,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Forgot password link
                    Text(
                        text = "Quên mật khẩu?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToForgotPassword)
                            .padding(bottom = 24.dp)
                    )

                    // Login button
                    CustomButton(
                        text = "Đăng nhập",
                        onClick = viewModel::login,
                        isLoading = state.isLoading,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Register link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chưa có tài khoản? ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Đăng ký ngay",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = onNavigateToRegister)
                        )
                    }
                }
            }
        }

        // Show error if there is one
        if (state.error != null) {
            AlertDialog(
                onDismissRequest = viewModel::clearError,
                title = { Text("Lỗi", style = MaterialTheme.typography.titleMedium) },
                text = { Text(state.error!!, style = MaterialTheme.typography.bodyMedium) },
                confirmButton = {
                    TextButton(onClick = viewModel::clearError) {
                        Text("OK", color = MaterialTheme.colorScheme.primary)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Loading indicator
        LoadingDialog(isLoading = state.isLoading)
    }
}