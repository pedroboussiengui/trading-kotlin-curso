package org.example.application.usecase

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.domain.Order
import org.example.infra.http.routes.ConnectionManager
import org.example.infra.repository.OrderRepository

class PlaceOrder(
    val orderRepository: OrderRepository,
    private val connectionManager: ConnectionManager
) {
    fun execute(input: OrderInput): OrderOutput {
        val order = Order.create(
            input.marketId,
            input.accountId,
            input.side,
            input.quantity,
            input.price
        )
        orderRepository.saveOrder(order)
        val executeOrder = ExecuteOrder(orderRepository)
        executeOrder.execute(input.marketId)
        val getDepth = GetDepth(orderRepository)
        val depth = getDepth.execute(input.marketId, 0)
        val depthJson = Json.encodeToString(depth)

        runBlocking {
            connectionManager.broadcast(depthJson)
        }

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