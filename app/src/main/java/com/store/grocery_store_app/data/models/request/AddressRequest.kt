package com.store.grocery_store_app.data.models.request

data class AddressRequest (
    val city: String,
    val district: String,
    val streetAddress: String,
    val userName: String,
    val phoneNumber: String,
    val isDefault: Boolean = false
)