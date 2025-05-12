package com.store.grocery_store_app.ui.screens.voucher

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.screens.voucher.components.VoucherCard

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    viewModel: VoucherViewModel = hiltViewModel(),
    onConfirm: (selected: Map<String, Long?>) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedMap by viewModel.selectedVoucherIds.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chọn voucher") }
            )
        },
        bottomBar = {
            Button(
                onClick = { onConfirm(selectedMap) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Đồng ý")
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            if (uiState.vouchersFreeship.isNotEmpty()) {
                item {
                    Text(
                        text = "Voucher Freeship",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(uiState.vouchersFreeship) { voucher ->
                    VoucherCard(
                        voucher = voucher,
                        isChecked = selectedMap["FREESHIP"] == voucher.id,
                        onCheckedChange = { viewModel.onVoucherChecked(voucher) }
                    )
                }
            }

            if (uiState.vouchersDiscount.isNotEmpty()) {
                item {
                    Text(
                        text = "Voucher Discount",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(uiState.vouchersDiscount) { voucher ->
                    VoucherCard(
                        voucher = voucher,
                        isChecked = selectedMap["DISCOUNT"] == voucher.id,
                        onCheckedChange = { viewModel.onVoucherChecked(voucher) }
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        CircularProgressIndicator()
                    }
                }
            }

            uiState.error?.let { errorMsg ->
                item {
                    Text(
                        text = "Lỗi: $errorMsg",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
