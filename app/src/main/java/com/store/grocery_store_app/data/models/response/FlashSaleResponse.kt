package com.store.grocery_store_app.data.models.response

import com.store.grocery_store_app.data.models.FlashSaleStatusType

data class FlashSaleResponse(
    val id:Long,
    val name:String,
    val description:String,
    val endTime:String,
    val startTime:String,
    val status: FlashSaleStatusType,
    val flashSaleItems: List<FlashSaleItemResponse>
)