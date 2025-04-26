package com.store.grocery_store_app.data.models.request

data class OtpRequest (
    val email: String,
    val forRegistration: Boolean
)