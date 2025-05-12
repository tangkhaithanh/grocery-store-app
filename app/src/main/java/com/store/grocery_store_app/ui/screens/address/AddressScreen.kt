package com.store.grocery_store_app.ui.screens.address

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng


// Data model for Address
data class Address(
    val id: String,
    val recipient: String,
    val phone: String,
    val street: String,
    val building: String,
    val province: String,
    val district: String,
    val ward: String,
    val latLng : LatLng?
)
private val DeepTeal = Color(0xFF004D40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    addresses: List<Address>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onEdit: (Long) -> Unit
) {
    var selected by remember { mutableStateOf(selectedId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Địa chỉ giao hàng") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepTeal
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(addresses) { address ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            selected = address.id
                            onSelect(address.id)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        RadioButton(
                            selected = (selected == address.id),
                            onClick = {
                                selected = address.id
                                onSelect(address.id)
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${address.recipient} - ${address.phone}",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${address.street}, ${address.building}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = "${address.province}, ${address.district}, ${address.ward}")
                        }
                        IconButton(onClick = { onEdit(address.id.toLong()) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            }
        }
    }
}




// ----- Previews with sample data -----

@Preview(showBackground = true)
@Composable
fun AddressListScreenPreview() {
    val sampleAddresses = listOf(
        Address(
            id = "1",
            recipient = "Nguyễn Văn A",
            phone = "0901234567",
            street = "123 Lý Thường Kiệt",
            building = "Tòa nhà ABCCCCCCCCCCCCCCCCCCCCCCCCCCC",
            province = "TP.HCM",
            district = "Quận 10",
            ward = "Phường 5",
            latLng = null
        ),
        Address(
            id = "2",
            recipient = "Trần Thị B",
            phone = "0912345678",
            street = "456 Hai Bà Trưng",
            building = "Chung cư XYZ",
            province = "Hà Nội",
            district = "Quận Hoàn Kiếm",
            ward = "Phường Hàng Bạc",
            latLng = null
        )
    )
    AddressListScreen(
        addresses = sampleAddresses,
        selectedId = "1",
        onSelect = {},
        onEdit = {}
    )
}
