package org.example

import kotlinx.serialization.Serializable

//@Serializable
//data class AccountInput(
//    val accountId: String? = null,
//    val name: String,
//    val email: String,
//    val document: String,
//    val password: String? = null,
//    val assets: MutableList<Asset> = mutableListOf()
//)
//
//@Serializable
//data class Asset(
//    val assetId: String,
//    val quantity: Int
//)

//@Serializable
//data class Deposit(
//    val accountId: String,
//    val assetId: String,
//    val quantity: Int
//)

//@Serializable
//data class Withdraw(
//    val accountId: String,
//    val assetId: String,
//    val quantity: Int
//)

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