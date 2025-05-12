package com.store.grocery_store_app.data.models.response

import java.math.BigDecimal
import java.time.LocalDate

data class VoucherResponse(
    val id:Long,
    val name:String,
    val code:String,
    val discount: BigDecimal,
    val expiryDate:String,
    val quantity:Int,
    val createAt:String,
    val type:String
)
