package com.store.grocery_store_app.data.models.request

data class ReviewRequest(
    val rating : Int,
    val comment : String?,
    val imageUrls : List<String>?
)


