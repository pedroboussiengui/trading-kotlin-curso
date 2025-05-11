package org.example

import kotlinx.serialization.Serializable

class Deposit(
    val accountRepository: AccountRepository
) {
    fun execute(input: DepositInput) {
        val accountAsset = AccountAsset(input.accountId, input.assetId, input.quantity)
        accountRepository.saveAccountAsset(accountAsset)
    }
}

@Serializable
data class DepositInput(
    val accountId: String,
    val assetId: String,
    val quantity: Int
)