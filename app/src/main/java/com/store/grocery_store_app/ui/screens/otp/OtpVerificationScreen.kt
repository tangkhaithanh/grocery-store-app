package com.store.grocery_store_app.ui.screens.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.CustomButton
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.utils.AuthPurpose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    purpose: AuthPurpose,
    email: String,
    onVerificationSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: OtpVerificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    // Initialize ViewModel with email and purpose
    LaunchedEffect(email, purpose) {
        viewModel.init(email, purpose)
        delay(100) // Short delay before requesting focus
        focusRequester.requestFocus()
    }

    // Handle verification success
    LaunchedEffect(state.isVerified) {
        if (state.isVerified) {
            onVerificationSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (purpose) {
                            AuthPurpose.REGISTRATION -> "Xác thực OTP để đăng ký"
                            AuthPurpose.PASSWORD_RESET -> "Xác thực OTP để đặt lại mật khẩu"
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
                            text = "Xác thực mã OTP",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Mã xác thực đã được gửi đến\n$email",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // OTP input fields
                        OtpTextField(
                            otpText = state.otp,
                            onOtpTextChange = viewModel::onOtpChange,
                            focusRequester = focusRequester,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Error message
                        if (state.otpError.isNotEmpty()) {
                            Text(
                                text = state.otpError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Verify button
                        CustomButton(
                            text = "Xác thực",
                            onClick = viewModel::verifyOtp,
                            isLoading = state.isLoading,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Resend OTP
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (state.resendCooldown > 0)
                                    "Gửi lại sau ${state.resendCooldown}s"
                                else
                                    "Không nhận được mã? ",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (state.resendCooldown <= 0) {
                                Text(
                                    text = "Gửi lại",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .clickable(
                                            enabled = !state.isLoading && state.resendCooldown <= 0
                                        ) {
                                            scope.launch {
                                                viewModel.resendOtp()
                                            }
                                        }
                                )
                            }
                        }
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

@Composable
fun OtpTextField(
    otpText: String,
    onOtpTextChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    otpCount: Int = 6
) {
    BasicTextField(
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount && it.all { char -> char.isDigit() }) {
                onOtpTextChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = modifier.focusRequester(focusRequester),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(otpCount) { index ->
                    val char = when {
                        index >= otpText.length -> ""
                        else -> otpText[index].toString()
                    }
                    val isFocused = otpText.length == index

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}