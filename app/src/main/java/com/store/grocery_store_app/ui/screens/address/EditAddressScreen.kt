package com.store.grocery_store_app.ui.screens.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.CustomButton
import com.store.grocery_store_app.ui.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressScreen(
    addressId: Long,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    addressViewModel: AddressViewModel = hiltViewModel()
) {
    val addressState by addressViewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Load address data when screen opens
    LaunchedEffect(addressId) {
        addressViewModel.loadAddressForEdit(addressId)
    }

    // Navigate back on success
    LaunchedEffect(addressState.updateSuccess) {
        if (addressState.updateSuccess) {
            addressViewModel.clearFormSuccessFlags()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa địa chỉ",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                addressState.isLoadingForm && addressState.currentAddress == null -> {
                    // Loading initial data
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                addressState.error != null && addressState.currentAddress == null -> {
                    // Error loading initial data
                    ErrorCard(
                        message = addressState.error!!,
                        onRetry = { addressViewModel.loadAddressForEdit(addressId) },
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    // Show form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // User Name Field
                        CustomTextField(
                            value = addressState.formUserName,
                            onValueChange = { addressViewModel.updateFormUserName(it) },
                            label = "Họ và tên",
                            placeholder = "Nhập họ và tên người nhận",
                            leadingIcon = Icons.Default.Person,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            isError = addressState.userNameError != null,
                            errorMessage = addressState.userNameError ?: ""
                        )

                        // Phone Number Field
                        CustomTextField(
                            value = addressState.formPhoneNumber,
                            onValueChange = { addressViewModel.updateFormPhoneNumber(it) },
                            label = "Số điện thoại",
                            placeholder = "Nhập số điện thoại",
                            leadingIcon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next,
                            isError = addressState.phoneNumberError != null,
                            errorMessage = addressState.phoneNumberError ?: ""
                        )

                        // City Field
                        CustomTextField(
                            value = addressState.formCity,
                            onValueChange = { addressViewModel.updateFormCity(it) },
                            label = "Tỉnh/Thành phố",
                            placeholder = "Nhập tỉnh/thành phố",
                            leadingIcon = Icons.Default.LocationCity,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            isError = addressState.cityError != null,
                            errorMessage = addressState.cityError ?: ""
                        )

                        // District Field
                        CustomTextField(
                            value = addressState.formDistrict,
                            onValueChange = { addressViewModel.updateFormDistrict(it) },
                            label = "Quận/Huyện",
                            placeholder = "Nhập quận/huyện",
                            leadingIcon = Icons.Default.LocationOn,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            isError = addressState.districtError != null,
                            errorMessage = addressState.districtError ?: ""
                        )

                        // Street Address Field
                        CustomTextField(
                            value = addressState.formStreetAddress,
                            onValueChange = { addressViewModel.updateFormStreetAddress(it) },
                            label = "Địa chỉ cụ thể",
                            placeholder = "Số nhà, tên đường, phường/xã",
                            leadingIcon = Icons.Default.Home,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                            isError = addressState.streetAddressError != null,
                            errorMessage = addressState.streetAddressError ?: ""
                        )

                        // Default Address Checkbox
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = addressState.formIsDefault,
                                onCheckedChange = { addressViewModel.updateFormIsDefault(it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đặt làm địa chỉ mặc định",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Error message if any
                        if (addressState.error != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = addressState.error!!,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save Button
                        CustomButton(
                            text = "Lưu thay đổi",
                            onClick = { addressViewModel.updateAddress() },
                            isLoading = addressState.isLoadingForm,
                            enabled = !addressState.isLoadingForm
                        )

                        // Bottom spacing
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}