package org.example.domain

import java.time.LocalDateTime
import java.util.*

data class Order(
    val orderId: String,
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Double,
    var status: String,
    val timestamp: LocalDateTime,
    var fillQuantity: Int = 0,
    var fillPrice: Double = 0.0
) {
    companion object {
        fun create(
            marketId: String,
            accountId: String,
            side: String,
            quantity: Int,
            price: Double
        ): Order {
            val orderId = UUID.randomUUID().toString()
            val status = "open"
            val timestamp = LocalDateTime.now()
            val fillQuantity = 0
            val fillPrice = 0.0
            return Order(orderId, marketId, accountId, side, quantity, price, status, timestamp, fillQuantity, fillPrice)
        }
    }

    fun fill(quantity: Int, price: Double) {
        this.fillPrice = ((this.fillQuantity * this.fillPrice) + (quantity * price)) / (this.fillQuantity + quantity)
        this.fillQuantity += quantity
        if (getAvailableQuantity() == 0) {
            this.status = "closed"
        }
    }

    fun getAvailableQuantity(): Int {
        return this.quantity - this.fillQuantity
    }
}