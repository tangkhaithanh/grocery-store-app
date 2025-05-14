package com.store.grocery_store_app.ui.screens.address
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.store.grocery_store_app.data.models.request.AddressRequest
import com.store.grocery_store_app.data.models.response.AddressDTO
import com.store.grocery_store_app.data.repository.AddressRepository
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddressState(
    val addresses: List<AddressDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteSuccess: Boolean = false,
    val setDefaultSuccess: Boolean = false,

    // Add/Edit form fields
    val currentAddress: AddressDTO? = null,
    val formUserName: String = "",
    val formPhoneNumber: String = "",
    val formCity: String = "",
    val formDistrict: String = "",
    val formStreetAddress: String = "",
    val formIsDefault: Boolean = false,

    // Form validation errors
    val userNameError: String? = null,
    val phoneNumberError: String? = null,
    val cityError: String? = null,
    val districtError: String? = null,
    val streetAddressError: String? = null,

    // Form states
    val createSuccess: Boolean = false,
    val updateSuccess: Boolean = false,
    val isLoadingForm: Boolean = false
)

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddressState())
    val state: StateFlow<AddressState> = _state.asStateFlow()

    fun loadAddresses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            addressRepository.getAllAddresses().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                addresses = result.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
    fun setDefaultAddress(addressId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Call API to set default
            addressRepository.setDefaultAddress(addressId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Wait for backend to update
                        delay(500)
                        // Reload addresses to get fresh data
                        loadAddresses()
                        _state.update { it.copy(setDefaultSuccess = true) }

                        // Clear success flag
                        delay(1000)
                        _state.update { it.copy(setDefaultSuccess = false) }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Keep loading state
                    }
                }
            }
        }
    }

    fun deleteAddress(addressId: Long) {
        viewModelScope.launch {
            addressRepository.deleteAddress(addressId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        // Reload sau khi xóa thành công
                        delay(500)
                        loadAddresses()
                        _state.update {
                            it.copy(
                                deleteSuccess = true,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadAddressForEdit(addressId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingForm = true, error = null) }

            addressRepository.getAddressById(addressId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoadingForm = true) }
                    }
                    is Resource.Success -> {
                        result.data?.let { address ->
                            _state.update {
                                it.copy(
                                    isLoadingForm = false,
                                    currentAddress = address,
                                    formUserName = address.userName,
                                    formPhoneNumber = address.phoneNumber,
                                    formCity = address.city,
                                    formDistrict = address.district,
                                    formStreetAddress = address.streetAddress,
                                    formIsDefault = address.isDefault,
                                    error = null
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoadingForm = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearForm() {
        _state.update {
            it.copy(
                currentAddress = null,
                formUserName = "",
                formPhoneNumber = "",
                formCity = "",
                formDistrict = "",
                formStreetAddress = "",
                formIsDefault = false,
                userNameError = null,
                phoneNumberError = null,
                cityError = null,
                districtError = null,
                streetAddressError = null,
                createSuccess = false,
                updateSuccess = false,
                error = null
            )
        }
    }
    // Form field updates
    fun updateFormUserName(userName: String) {
        _state.update {
            it.copy(
                formUserName = userName,
                userNameError = if (userName.isBlank()) "Họ tên không được để trống" else null
            )
        }
    }

    fun updateFormPhoneNumber(phoneNumber: String) {
        _state.update {
            it.copy(
                formPhoneNumber = phoneNumber,
                phoneNumberError = validatePhoneNumber(phoneNumber)
            )
        }
    }

    fun updateFormCity(city: String) {
        _state.update {
            it.copy(
                formCity = city,
                cityError = if (city.isBlank()) "Tỉnh/Thành phố không được để trống" else null
            )
        }
    }

    fun updateFormDistrict(district: String) {
        _state.update {
            it.copy(
                formDistrict = district,
                districtError = if (district.isBlank()) "Quận/Huyện không được để trống" else null
            )
        }
    }

    fun updateFormStreetAddress(streetAddress: String) {
        _state.update {
            it.copy(
                formStreetAddress = streetAddress,
                streetAddressError = if (streetAddress.isBlank()) "Địa chỉ cụ thể không được để trống" else null
            )
        }
    }

    fun updateFormIsDefault(isDefault: Boolean) {
        _state.update { it.copy(formIsDefault = isDefault) }
    }

    // Create new address
    fun createAddress() {
        val currentState = _state.value

        // Validate all fields
        if (!validateForm()) return

        val addressRequest = AddressRequest(
            city = currentState.formCity.trim(),
            district = currentState.formDistrict.trim(),
            streetAddress = currentState.formStreetAddress.trim(),
            userName = currentState.formUserName.trim(),
            phoneNumber = currentState.formPhoneNumber.trim(),
            isDefault = currentState.formIsDefault
        )

        viewModelScope.launch {
            _state.update { it.copy(isLoadingForm = true, error = null) }

            addressRepository.createAddress(addressRequest).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoadingForm = true) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoadingForm = false,
                                createSuccess = true,
                                error = null
                            )
                        }
                        // Reload addresses list
                        delay(500)
                        loadAddresses()
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoadingForm = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    // Update existing address
    fun updateAddress() {
        val currentState = _state.value
        val addressId = currentState.currentAddress?.id ?: return

        // Validate all fields
        if (!validateForm()) return

        val addressRequest = AddressRequest(
            city = currentState.formCity.trim(),
            district = currentState.formDistrict.trim(),
            streetAddress = currentState.formStreetAddress.trim(),
            userName = currentState.formUserName.trim(),
            phoneNumber = currentState.formPhoneNumber.trim(),
            isDefault = currentState.formIsDefault
        )

        viewModelScope.launch {
            _state.update { it.copy(isLoadingForm = true, error = null) }

            addressRepository.updateAddress(addressId, addressRequest).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoadingForm = true) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoadingForm = false,
                                updateSuccess = true,
                                error = null
                            )
                        }
                        // Reload addresses list
                        delay(500)
                        loadAddresses()
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoadingForm = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val currentState = _state.value

        val userNameError = if (currentState.formUserName.isBlank()) "Họ tên không được để trống" else null
        val phoneNumberError = validatePhoneNumber(currentState.formPhoneNumber)
        val cityError = if (currentState.formCity.isBlank()) "Tỉnh/Thành phố không được để trống" else null
        val districtError = if (currentState.formDistrict.isBlank()) "Quận/Huyện không được để trống" else null
        val streetAddressError = if (currentState.formStreetAddress.isBlank()) "Địa chỉ cụ thể không được để trống" else null

        _state.update {
            it.copy(
                userNameError = userNameError,
                phoneNumberError = phoneNumberError,
                cityError = cityError,
                districtError = districtError,
                streetAddressError = streetAddressError
            )
        }

        return userNameError == null && phoneNumberError == null && cityError == null &&
                districtError == null && streetAddressError == null
    }

    private fun validatePhoneNumber(phoneNumber: String): String? {
        return when {
            phoneNumber.isBlank() -> "Số điện thoại không được để trống"
            !phoneNumber.matches(Regex("^[0-9]{10,11}$")) -> "Số điện thoại không hợp lệ"
            else -> null
        }
    }

    // Clear form success flags
    fun clearFormSuccessFlags() {
        _state.update {
            it.copy(
                createSuccess = false,
                updateSuccess = false
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun clearSuccessFlags() {
        _state.update {
            it.copy(
                deleteSuccess = false,
                setDefaultSuccess = false
            )
        }
    }
}