package com.store.grocery_store_app.ui.screens.address

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Simple UI state holder
data class AddressUiState(
    val address: String? = null,
    val latLng: LatLng? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {
    private val _addressState = MutableStateFlow<AddressUiState>(AddressUiState())
    val addressState: StateFlow<AddressUiState> = _addressState

    /**
     * Geocode full address string into LatLng.
     * Calls [callback] on UI thread.
     */
    fun geocodeAddress(fullAddress: String, callback: (LatLng) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(app.applicationContext)
                    val list = geocoder.getFromLocationName(fullAddress, 1)
                    if (!list.isNullOrEmpty()) {
                        val loc = list[0]
                        LatLng(loc.latitude, loc.longitude)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            result?.let { callback(it) }
        }
    }
}