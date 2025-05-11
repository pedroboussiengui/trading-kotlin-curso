package org.example

class AccountAsset(
    val accountId: String,
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