package org.example

import java.time.LocalDate
import java.util.UUID

class PlaceOrder(
    val orderDAO: OrderDAO
) {
    fun execute(input: Order): Order {
        val order = input.copy(
            orderId = UUID.randomUUID().toString(),
            status = "open",
            timestamp = LocalDate.now().toString()
        )
        orderDAO.saveOrder(order)
        return order
    }
}