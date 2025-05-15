package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.domain.groupOrders
import org.example.infra.repository.OrderRepository

class GetDepth(
    val orderRepository: OrderRepository
) {
    fun execute(marketId: String, precision: Int): GetDepthOutput {
        val orders = orderRepository.getOrderByMarketIdAndStatus(marketId, "open")
        val output = GetDepthOutput(mutableListOf(), mutableListOf())
        val index = groupOrders(orders, precision)
        index["buy"]?.forEach { (price, quantity) ->
            output.buys.add(GetDepthOutput.Order(quantity, price))
        }
        index["sell"]?.forEach { (price, quantity) ->
            output.sells.add(GetDepthOutput.Order(quantity, price))
        }
        return output
    }
}

@Serializable
data class GetDepthOutput(
    val buys: MutableList<Order>,
    val sells: MutableList<Order>
) {
    @Serializable
    data class Order(val quantity: Int, val price: Int)
}