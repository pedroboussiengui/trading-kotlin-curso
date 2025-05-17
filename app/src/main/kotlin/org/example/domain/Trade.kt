package org.example.domain

import java.time.Instant
import java.util.*

class Trade(
    val tradeId: UUID,
    val marketId: String,
    val buyOrderId: UUID,
    val sellOrderId: UUID,
    val side: String,
    val quantity: Int,
    val price: Double,
    val timestamp: Instant
) {
    companion object {
        fun create(
            marketId: String,
            buyOrderId: UUID,
            sellOrderId: UUID,
            side: String,
            quantity: Int,
            price: Double
        ): Trade {
            val tradeId = UUID.randomUUID()
            val timestamp = Instant.now()
            return Trade(tradeId, marketId, buyOrderId, sellOrderId, side, quantity, price, timestamp)
        }
    }
}