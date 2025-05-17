package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.domain.Order
import org.example.infra.mediator.Mediator
import org.example.infra.repository.OrderRepository
import java.util.UUID

class PlaceOrder(
    val orderRepository: OrderRepository,
    val mediator: Mediator = Mediator()
) {
    fun execute(input: OrderInput): OrderOutput {
        val order = Order.create(
            input.marketId,
            UUID.fromString(input.accountId),
            input.side,
            input.quantity,
            input.price
        )
        println(order)
        orderRepository.saveOrder(order)
        mediator.notifyAll<Unit>("orderPlaced", input.marketId)
        return OrderOutput(order.orderId.toString())
    }
}

@Serializable
data class OrderInput(
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class OrderOutput(
    val orderId: String
)