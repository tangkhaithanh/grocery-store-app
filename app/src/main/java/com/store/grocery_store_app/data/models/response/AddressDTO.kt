package com.store.grocery_store_app.data.models.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName
@Parcelize
data class AddressDTO(
    val id: Long,
    val userId: Long,
    val city: String,
    val district: String,
    val streetAddress: String,
    val userName: String,
    val phoneNumber: String,
    @SerializedName("default")
    val isDefault: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null
): Parcelable