package com.store.grocery_store_app.ui.screens.EmailVerification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.CustomButton
import com.store.grocery_store_app.ui.components.CustomTextField
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.utils.AuthPurpose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(
    purpose: AuthPurpose,
    onNavigateToOtp: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Set the purpose in ViewModel
    LaunchedEffect(purpose) {
        viewModel.setPurpose(purpose)
    }

    // Navigate to OTP screen when email is verified
    LaunchedEffect(state.otpSent) {
        if (state.otpSent) {
            onNavigateToOtp(state.email)
            viewModel.resetOtpSentState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (purpose) {
                            AuthPurpose.REGISTRATION -> "Đăng ký"
                            AuthPurpose.PASSWORD_RESET -> "Quên mật khẩu"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (purpose == AuthPurpose.REGISTRATION)
                                "Xác thực email để đăng ký"
                            else
                                "Xác thực email để lấy lại mật khẩu",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Vui lòng nhập địa chỉ email của bạn để nhận mã OTP xác thực",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
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
                            imeAction = ImeAction.Done,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Send OTP button
                        CustomButton(
                            text = "Gửi mã xác thực",
                            onClick = viewModel::sendOtp,
                            isLoading = state.isLoading
                        )
                    }
                }
            }

            // Show error if there is one
            if (state.error != null) {
                AlertDialog(
                    onDismissRequest = viewModel::clearError,
                    title = { Text("Lỗi") },
                    text = { Text(state.error!!) },
                    confirmButton = {
                        TextButton(onClick = viewModel::clearError) {
                            Text("OK")
                        }
                    }
                )
            }

            // Loading indicator
            LoadingDialog(isLoading = state.isLoading)
        }
    }
}