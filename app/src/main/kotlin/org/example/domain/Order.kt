package org.example.domain

import java.time.LocalDateTime
import java.util.*

data class Order(
    val orderId: String,
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Int,
    var status: String,
    val timestamp: String,
    var fillQuantity: Int = 0,
    var fillPrice: Int = 0
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
            val timestamp = LocalDateTime.now().toString()
            val fillQuantity = 0
            val fillPrice = 0
            return Order(orderId, marketId, accountId, side, quantity, price, status, timestamp, fillQuantity, fillPrice)
        }
    }
}