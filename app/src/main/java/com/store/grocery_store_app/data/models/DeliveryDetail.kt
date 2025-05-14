package com.store.grocery_store_app.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DeliveryDetail(
    val statusOrderType: StatusOrderType,
    val deliveredAt: String?,
): Parcelable