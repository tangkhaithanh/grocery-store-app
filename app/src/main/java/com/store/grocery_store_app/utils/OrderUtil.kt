package com.store.grocery_store_app.utils

import com.store.grocery_store_app.data.models.OrderGroup
import com.store.grocery_store_app.data.models.OrderItem

object OrderUtil {
    fun groupOrders(orderItems: List<OrderItem>): List<OrderGroup> {
        return orderItems.groupBy { it.orderId }.map { (orderId, items) ->
            OrderGroup(
                orderId = orderId,
                storeName = items.first().storeName,
                items = items,
                totalAmount = items.sumOf { it.totalAmount }
            )
        }
    }

}