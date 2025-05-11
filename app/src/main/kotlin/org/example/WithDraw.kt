package org.example

import kotlinx.serialization.Serializable

class WithDraw(
    val accountRepository: AccountRepository
) {
    fun execute(input: WithDrawInput) {
        val accountAsset: AccountAsset = accountRepository.getAccountAsset(input.accountId, input.assetId)
        accountAsset.withdraw(input.quantity)
        accountRepository.updateAccountAsset(accountAsset)
    }
}

@Serializable
data class WithDrawInput(
    val accountId: String,
    val assetId: String,
    val quantity: Int
)