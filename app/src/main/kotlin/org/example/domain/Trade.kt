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
    val price: Int,
    val timestamp: String
) {
    companion object {
        fun create(
            marketId: String,
            buyOrderId: String,
            sellOrderId: String,
            side: String,
            quantity: Int,
            price: Int
        ): Trade {
            val tradeId = UUID.randomUUID().toString()
            val timestamp = LocalDateTime.now().toString()
            return Trade(tradeId, marketId, buyOrderId, sellOrderId, side, quantity, price, timestamp)
        }
    }
}