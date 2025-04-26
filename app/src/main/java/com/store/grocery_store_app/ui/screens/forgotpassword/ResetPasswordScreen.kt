package com.store.grocery_store_app.ui.screens.forgotpassword
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.CustomButton
import com.store.grocery_store_app.ui.components.CustomTextField
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.ui.screens.forgotpassword.ResetPasswordViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    email: String,
    onResetSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Initialize the email from navigation
    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    // Handle reset success
    LaunchedEffect(state.isReset) {
        if (state.isReset) {
            onResetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đặt lại mật khẩu",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 32.dp, start = 8.dp, end = 8.dp),
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
                            text = "Đặt lại mật khẩu",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Tạo mật khẩu mới cho tài khoản của bạn",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // Email field (disabled, pre-filled from verification)
                        CustomTextField(
                            value = state.email,
                            onValueChange = { /* Do nothing, email is fixed from OTP verification */ },
                            label = "Email",
                            leadingIcon = Icons.Default.Email,
                            imeAction = ImeAction.Next,
                            modifier = Modifier.padding(bottom = 16.dp),
                            enabled = false
                        )

                        // New Password field
                        CustomTextField(
                            value = state.newPassword,
                            onValueChange = viewModel::onNewPasswordChange,
                            label = "Mật khẩu mới",
                            placeholder = "Nhập mật khẩu mới của bạn",
                            leadingIcon = Icons.Default.Lock,
                            isError = state.newPasswordError.isNotEmpty(),
                            errorMessage = state.newPasswordError,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                            isPassword = true,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Confirm New Password field
                        CustomTextField(
                            value = state.confirmNewPassword,
                            onValueChange = viewModel::onConfirmNewPasswordChange,
                            label = "Xác nhận mật khẩu mới",
                            placeholder = "Nhập lại mật khẩu mới của bạn",
                            leadingIcon = Icons.Default.Lock,
                            isError = state.confirmNewPasswordError.isNotEmpty(),
                            errorMessage = state.confirmNewPasswordError,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            isPassword = true,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // Reset Password button
                        CustomButton(
                            text = "Đặt lại mật khẩu",
                            onClick = viewModel::resetPassword,
                            isLoading = state.isLoading
                        )
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
}