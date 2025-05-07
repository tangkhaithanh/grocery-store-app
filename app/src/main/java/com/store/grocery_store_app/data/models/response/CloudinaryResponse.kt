package com.store.grocery_store_app.data.models.response

import com.google.gson.annotations.SerializedName

data class CloudinaryResponse(
    @SerializedName("url") val url: String
)
