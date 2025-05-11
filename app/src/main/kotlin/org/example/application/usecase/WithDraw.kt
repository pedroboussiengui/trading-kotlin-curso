package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.domain.AccountAsset
import org.example.infra.repository.AccountRepository

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