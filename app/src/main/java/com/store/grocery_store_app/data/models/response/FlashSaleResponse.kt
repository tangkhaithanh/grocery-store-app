package com.store.grocery_store_app.data.models.response

data class FlashSaleResponse(
    val id:Long,
    val name:String,
    val description:String,
    val endTime:String,
    val startTime:String,
    val flashSaleItems: List<FlashSaleItemResponse>
)