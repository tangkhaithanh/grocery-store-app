package com.store.grocery_store_app.ui.screens.address

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressScreen(
    address: Address,
    viewModel: AddressViewModel = hiltViewModel(),
    onSave: (Address) -> Unit
) {
    var recipient by remember { mutableStateOf(address.recipient) }
    var phone by remember { mutableStateOf(address.phone) }
    var street by remember { mutableStateOf(address.street) }
    var building by remember { mutableStateOf(address.building) }
    var province by remember { mutableStateOf(address.province) }
    var district by remember { mutableStateOf(address.district) }
    var ward by remember { mutableStateOf(address.ward) }
    var markerPosition by remember { mutableStateOf(address.latLng ?: LatLng(10.762622, 106.660172)) }
    var isLoading by remember { mutableStateOf(false) }

    // Use rememberMarkerState since rememberUpdatedMarkerState isn't available in v5
    val markerState = rememberMarkerState(position = markerPosition)
    // Sync position back to our local variable when dragged
    LaunchedEffect(markerState.position) {
        markerPosition = markerState.position
    }

    val provincesList = listOf("TP.HCM", "Hà Nội", "Đà Nẵng")
    val districtsList = listOf("Quận 1", "Quận 10", "Quận 3")
    val wardsList = listOf("Phường 1", "Phường 5", "Phường 10")

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa địa chỉ", color = Color(0xFF004D40)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF004D40)
                ),
                actions = {
                    TextButton(onClick = {
                        val updated = address.copy(
                            recipient = recipient,
                            phone = phone,
                            street = street,
                            building = building,
                            province = province,
                            district = district,
                            ward = ward,
                            latLng = markerPosition
                        )
                        onSave(updated)
                    }) {
                        Text("Lưu", color = Color(0xFF004D40))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text("Người nhận") },
                singleLine = true
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                singleLine = true
            )
            SuggestionDropdown(
                label = "Tỉnh/Thành phố",
                options = provincesList,
                selected = province,
                onSelect = { province = it }
            )
            SuggestionDropdown(
                label = "Quận/Huyện",
                options = districtsList,
                selected = district,
                onSelect = { district = it }
            )
            SuggestionDropdown(
                label = "Phường/Xã",
                options = wardsList,
                selected = ward,
                onSelect = {
                    ward = it
                    isLoading = true
                    val fullAddress = listOf(street, ward, district, province).joinToString(", ")
                    viewModel.geocodeAddress(fullAddress) { latLng ->
                        markerPosition = latLng
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                        isLoading = false
                    }
                }
            )
            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Số đường") },
                singleLine = true
            )
            OutlinedTextField(
                value = building,
                onValueChange = { building = it },
                label = { Text("Tòa nhà") }
            )
            Box(modifier = Modifier.weight(1f)) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    Marker(
                        state = markerState,
                        draggable = true
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF004D40)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onSelect(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditAddressScreenPreview() {
    val sample = Address(
        id = "1",
        recipient = "Nguyễn Văn A",
        phone = "0901234567",
        street = "123 Lý Thường Kiệt",
        building = "Tòa nhà ABC",
        province = "TP.HCM",
        district = "Quận 10",
        ward = "Phường 5",
        latLng = LatLng(10.762622, 106.660172)
    )
    EditAddressScreen(
        address = sample,
        onSave = {}
    )
}