package com.store.grocery_store_app.ui.screens.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.CustomButton
import com.store.grocery_store_app.ui.components.CustomTextField
import com.store.grocery_store_app.ui.components.LoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    email: String,
    onRegistrationSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Initialize the email from navigation
    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    // Handle registration success
    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onRegistrationSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng ký tài khoản") },
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
                horizontalAlignment = Alignment.CenterHorizontally
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
                        // Full Name field
                        CustomTextField(
                            value = state.fullName,
                            onValueChange = viewModel::onFullNameChange,
                            label = "Họ và tên",
                            placeholder = "Nhập họ và tên của bạn",
                            leadingIcon = Icons.Default.Person,
                            isError = state.fullNameError.isNotEmpty(),
                            errorMessage = state.fullNameError,
                            imeAction = ImeAction.Next,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Phone field
                        CustomTextField(
                            value = state.phone,
                            onValueChange = viewModel::onPhoneChange,
                            label = "Số điện thoại",
                            placeholder = "Nhập số điện thoại của bạn",
                            leadingIcon = Icons.Default.Phone,
                            isError = state.phoneError.isNotEmpty(),
                            errorMessage = state.phoneError,
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Gender selection
                        GenderSelection(
                            selectedGender = state.gender,
                            onGenderSelected = viewModel::onGenderChange,
                            isError = state.genderError.isNotEmpty(),
                            errorMessage = state.genderError,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Email field (disabled, pre-filled from verification)
                        CustomTextField(
                            value = state.email,
                            onValueChange = { /* Do nothing, email is fixed from OTP verification */ },
                            label = "Email",
                            leadingIcon = Icons.Default.Email,
                            imeAction = ImeAction.Next,
                            modifier = Modifier.padding(bottom = 8.dp),
                            enabled = false
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
                            imeAction = ImeAction.Next,
                            isPassword = true,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Confirm Password field
                        CustomTextField(
                            value = state.confirmPassword,
                            onValueChange = viewModel::onConfirmPasswordChange,
                            label = "Xác nhận mật khẩu",
                            placeholder = "Nhập lại mật khẩu của bạn",
                            leadingIcon = Icons.Default.Lock,
                            isError = state.confirmPasswordError.isNotEmpty(),
                            errorMessage = state.confirmPasswordError,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            isPassword = true,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Register button
                        CustomButton(
                            text = "Đăng ký",
                            onClick = viewModel::register,
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

@Composable
fun GenderSelection(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Giới tính",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenderOption(
                text = "Nam",
                selected = selectedGender == "Nam",
                onClick = { onGenderSelected("Nam") },
                modifier = Modifier.weight(1f)
            )

            GenderOption(
                text = "Nữ",
                selected = selectedGender == "Nữ",
                onClick = { onGenderSelected("Nữ") },
                modifier = Modifier.weight(1f)
            )

            GenderOption(
                text = "Khác",
                selected = selectedGender == "Khác",
                onClick = { onGenderSelected("Khác") },
                modifier = Modifier.weight(1f)
            )
        }

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun GenderOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(end = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}