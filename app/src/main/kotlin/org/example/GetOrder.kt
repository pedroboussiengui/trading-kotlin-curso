package org.example

class GetOrder(
    val orderDAO: OrderDAO
) {
    fun execute(orderId: String): Order? {
        return orderDAO.getOrderById(orderId)
    }
}