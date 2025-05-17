package org.example.domain

import java.util.UUID

class AccountAsset(
    val accountId: UUID,
    val assetId: String,
    private var quantity: Int
) {
    private fun hasEnoughFunds(quantity: Int): Boolean {
        return this.quantity >= quantity
    }

    fun withdraw(quantity: Int) {
        if (!hasEnoughFunds(quantity)) {
            throw Exception("Insufficient funds")
        }
        this.quantity -= quantity
    }

    fun getQuantity(): Int = quantity
}