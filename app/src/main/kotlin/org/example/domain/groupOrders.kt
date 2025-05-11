package org.example.domain

import kotlin.math.pow

fun groupOrders(orders: List<Order>, precision: Int): MutableMap<String, MutableMap<Int, Int>> {
    val index = mutableMapOf<String, MutableMap<Int, Int>>()
    for (order in orders) {
        var price = order.price
        if (precision > 0) {
            price -= price % 10.0.pow(precision).toInt()
        }
        index.getOrPut(order.side) { mutableMapOf() }[price] =
            index[order.side]?.get(price)?.plus(order.quantity) ?: order.quantity
    }
    return index
}