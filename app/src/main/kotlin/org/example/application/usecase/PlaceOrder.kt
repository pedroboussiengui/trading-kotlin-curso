package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.domain.Order
import org.example.infra.repository.OrderRepository

class PlaceOrder(
    val orderRepository: OrderRepository
) {
    fun execute(input: OrderInput): OrderOutput {
        val order = Order.Companion.create(
            input.marketId,
            input.accountId,
            input.side,
            input.quantity,
            input.price
        )
        orderRepository.saveOrder(order)
        return OrderOutput(order.orderId)
    }
}

@Serializable
data class OrderInput(
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Int
)

@Serializable
data class OrderOutput(
    val orderId: String
)