package com.store.grocery_store_app.ui.screens.voucher

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.response.VoucherResponse
import com.store.grocery_store_app.ui.components.ErrorDialog
import com.store.grocery_store_app.ui.screens.voucher.components.VoucherCard

private val DeepTeal = Color(0xFF004D40)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    viewModel: VoucherViewModel = hiltViewModel(),
    onConfirm: (VoucherResponse) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedVoucherIdFromVM by viewModel.selectedVoucherId.collectAsState() // Lấy state ở đây

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Freeship", "Discount")

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Chọn Voucher", color = Color.Black) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = DeepTeal
                            )
                        }
                    },
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = DeepTeal,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = DeepTeal
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    val voucher = viewModel.getSelectedVoucher()
                    if (voucher == null) {
                        viewModel.showError("Vui lòng chọn voucher!!!")
                    } else {
                        Log.d("Voucher", voucher.toString())

                        onConfirm(voucher)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
            ) {
                Text("Xác nhận", color = Color.White)
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (uiState.error != null) {
            ErrorDialog(
                title = "Vui lòng",
                content = uiState.error!!,
                clearError = viewModel::clearError
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.vouchersFreeship,
                            key = { voucher -> voucher.id }
                        ) { voucher ->
                            VoucherCard(
                                voucher = voucher,
                                currentSelectedId = selectedVoucherIdFromVM, // Truyền ID đang được chọn
                                onVoucherClick = { viewModel.onVoucherChecked(voucher) } // Đổi tên prop
                            )
                        }
                        if (uiState.isLoading) item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = DeepTeal)
                            }
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.vouchersDiscount,
                            key = { voucher -> voucher.id }
                        ) { voucher ->
                            VoucherCard(
                                voucher = voucher,
                                currentSelectedId = selectedVoucherIdFromVM, // Truyền ID đang được chọn
                                onVoucherClick = { viewModel.onVoucherChecked(voucher) } // Đổi tên prop
                            )
                        }
                        if (uiState.isLoading) item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = DeepTeal)
                            }
                        }
                    }
                }
            }
        }
    }
}