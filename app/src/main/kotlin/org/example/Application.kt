package org.example

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*

@Serializable
data class Account(
    val accountId: String? = null,
    val name: String,
    val email: String,
    val document: String,
    val password: String? = null,
    val assets: MutableList<Asset> = mutableListOf()
)

@Serializable
data class Asset(
    val assetId: String,
    val quantity: Int
)

@Serializable
data class Deposit(
    val accountId: String,
    val assetId: String,
    val quantity: Int
)

@Serializable
data class Withdraw(
    val accountId: String,
    val assetId: String,
    val quantity: Int
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class Order(
    val orderId: String? = null,
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Int,
    val status: String? = null,
    val timestamp: String? = null
)

//fun isValidName(name: String): Boolean {
//    return name.split(" ").size == 2
//}
//
//fun isValidEmail(email: String): Boolean {
//    val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
//    return emailRegex.matches(email)
//}
//
//fun signup(input: Account): Account {
//    if (!isValidName(input.name)) {
//        throw Exception("Invalid name")
//    }
//    if (!isValidEmail(input.email)) {
//        throw Exception("Invalid email")
//    }
//    if (!isValidPassword(input.password!!)) {
//        throw Exception("Invalid password")
//    }
//    if (!validateCpf(input.document)) {
//        throw Exception("Invalid document")
//    }
//    val account = input.copy(accountId = UUID.randomUUID().toString())
//    saveAccount(account)
//    return account
//}

fun deposit(input: Deposit) {
    saveAccountAsset(input)
}

fun withdraw(input: Withdraw) {
    val accountAssetData = getAccountAsset(input.accountId, input.assetId)
    val currentQuantity = accountAssetData?.quantity
    if (accountAssetData == null || currentQuantity!! < input.quantity) {
        throw Exception("Insufficient funds")
    }
    val quantity = currentQuantity - input.quantity
    updateAccountAsset(quantity, input.accountId, input.assetId)
}

fun placeOrder(input: Order): Order {
    val order = input.copy(orderId = UUID.randomUUID().toString(), status = "open", timestamp = LocalDate.now().toString())
    saveOrder(order)
    return order
}

fun getOrder(orderId: String): Order? {
    val order = getOrderById(orderId)
    return order
}

//fun getAccount(accountId: String): Account? {
//    val account = getAccountById(accountId)
//    val assets = getAccountAssets(accountId)
//    for (asset in assets) {
//        account?.assets?.add(asset)
//    }
//    return account
//}