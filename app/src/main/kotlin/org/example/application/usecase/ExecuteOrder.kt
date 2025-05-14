package org.example.application.usecase

import org.example.domain.Order
import org.example.infra.repository.OrderRepository
import java.time.LocalDateTime

class ExecuteOrder(
    val orderRepository: OrderRepository
) {
    fun execute(marketId: String) {
        // pega todas as ordens abertas
        val orders = orderRepository.getOrderByMarketIdAndStatus(marketId, "open")
        // pega a mais alta de compra
        val highestBuy = getHighestBuy(orders)
        // pega a mais baixa de venda
        val lowestSell = getLowestSell(orders)

        if (highestBuy != null && lowestSell != null && highestBuy.price >= lowestSell.price) {
            val fillQuantity = minOf(highestBuy.quantity, lowestSell.quantity)
            val fillPrice = if (LocalDateTime.parse(highestBuy.timestamp).isAfter(LocalDateTime.parse(lowestSell.timestamp))) {
                lowestSell.price
            } else {
                highestBuy.price
            }
            val tradeSide = if (LocalDateTime.parse(highestBuy.timestamp).isAfter(LocalDateTime.parse(lowestSell.timestamp)))
            { "buy" } else { "sell" }

            highestBuy.fillQuantity = fillQuantity
            lowestSell.fillQuantity = fillQuantity
            highestBuy.fillPrice = fillPrice
            lowestSell.fillPrice = fillPrice

            if (highestBuy.quantity == highestBuy.fillQuantity) {
                highestBuy.status = "closed"
            }

            if (lowestSell.quantity == lowestSell.fillQuantity) {
                lowestSell.status = "closed"
            }

            orderRepository.updateOrder(highestBuy)
            orderRepository.updateOrder(lowestSell)
        }
    }

    private fun getHighestBuy(orders: List<Order>): Order? {
        return orders.filter { it.side == "buy" }.maxByOrNull { it.price }
    }

    private fun getLowestSell(orders: List<Order>): Order? {
        return orders.filter { it.side == "sell" }.minByOrNull { it.price }
    }
}