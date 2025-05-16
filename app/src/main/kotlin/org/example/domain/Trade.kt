package org.example.domain

import java.time.LocalDateTime
import java.util.UUID

class Trade(
    val tradeId: String,
    val marketId: String,
    val buyOrderId: String,
    val sellOrderId: String,
    val side: String,
    val quantity: Int,
    val price: Double,
    val timestamp: LocalDateTime
) {
    companion object {
        fun create(
            marketId: String,
            buyOrderId: String,
            sellOrderId: String,
            side: String,
            quantity: Int,
            price: Double
        ): Trade {
            val tradeId = UUID.randomUUID().toString()
            val timestamp = LocalDateTime.now()
            return Trade(tradeId, marketId, buyOrderId, sellOrderId, side, quantity, price, timestamp)
        }
    }
}