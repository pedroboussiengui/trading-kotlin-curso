package org.example.application.usecase

import org.example.domain.Order
import org.example.domain.Trade
import org.example.infra.repository.OrderRepository
import org.example.infra.repository.TradeRepository
import java.time.LocalDateTime

class ExecuteOrder(
    val orderRepository: OrderRepository,
    val tradeRepository: TradeRepository
) {
    fun execute(marketId: String) {
        while (true) {
            val orders = orderRepository.getOrderByMarketIdAndStatus(marketId, "open")
            val highestBuy = getHighestBuy(orders)
            val lowestSell = getLowestSell(orders)
            highestBuy ?: return
            lowestSell ?: return
            if (highestBuy.price < lowestSell.price) return
            val fillQuantity = minOf(highestBuy.quantity, lowestSell.quantity)
            val fillPrice =
                if (LocalDateTime.parse(highestBuy.timestamp).isAfter(LocalDateTime.parse(lowestSell.timestamp))) {
                    lowestSell.price
                } else {
                    highestBuy.price
                }
            val tradeSide =
                if (LocalDateTime.parse(highestBuy.timestamp).isAfter(LocalDateTime.parse(lowestSell.timestamp))) {
                    "buy"
                } else {
                    "sell"
                }
            highestBuy.fill(fillQuantity, fillPrice)
            lowestSell.fill(fillQuantity, fillPrice)
            orderRepository.updateOrder(highestBuy)
            orderRepository.updateOrder(lowestSell)
            val trade = Trade.create(
                marketId,
                highestBuy.orderId,
                lowestSell.orderId,
                tradeSide,
                fillQuantity,
                fillPrice
            )
            tradeRepository.saveTrade(trade)
        }
    }

    private fun getHighestBuy(orders: List<Order>): Order? {
        return orders.filter { it.side == "buy" }.maxByOrNull { it.price }
    }

    private fun getLowestSell(orders: List<Order>): Order? {
        return orders.filter { it.side == "sell" }.minByOrNull { it.price }
    }
}