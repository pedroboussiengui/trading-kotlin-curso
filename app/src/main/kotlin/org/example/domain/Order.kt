package org.example.domain

import java.time.LocalDate
import java.util.UUID

class Order(
    val orderId: String,
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Int,
    val status: String,
    val timestamp: String
) {
    companion object {
        fun create(
            marketId: String,
            accountId: String,
            side: String,
            quantity: Int,
            price: Int
        ): Order {
            val orderId = UUID.randomUUID().toString()
            val status = "open"
            val timestamp = LocalDate.now().toString()
            return Order(orderId, marketId, accountId, side, quantity, price, status, timestamp)
        }
    }
}