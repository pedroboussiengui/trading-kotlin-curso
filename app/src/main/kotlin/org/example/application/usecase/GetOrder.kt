package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.infra.repository.OrderRepository

class GetOrder(
    val orderRepository: OrderRepository
) {
    fun execute(orderId: String): GetOrderOutput {
        val order = orderRepository.getOrderById(orderId)
        if (order == null) {
            throw Exception("Order not found")
        }
        return GetOrderOutput(
            order.orderId,
            order.marketId,
            order.accountId,
            order.side,
            order.quantity,
            order.price,
            order.status,
            order.timestamp,
            order.fillQuantity,
            order.fillPrice
        )
    }
}

@Serializable
data class GetOrderOutput(
    val orderId: String,
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Double,
    val status: String,
    val timestamp: String,
    val fillQuantity: Int,
    val fillPrice: Double
)