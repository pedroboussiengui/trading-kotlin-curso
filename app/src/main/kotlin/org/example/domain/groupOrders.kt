package org.example.domain

import kotlin.math.pow

fun groupOrders(orders: List<Order>, precision: Int): MutableMap<String, MutableMap<Double, Int>> {
    val index = mutableMapOf<String, MutableMap<Double, Int>>()
    for (order in orders) {
        var price = order.price
        if (precision > 0) {
            price -= price % 10.0.pow(precision).toInt()
        }
        index.getOrPut(order.side) { mutableMapOf() }[price] =
            index[order.side]?.get(price)?.plus(order.getAvailableQuantity()) ?: order.quantity
    }
    return index
}