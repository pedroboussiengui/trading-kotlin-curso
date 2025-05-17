package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.domain.AccountAsset
import org.example.infra.repository.AccountRepository
import java.util.UUID

class Deposit(
    val accountRepository: AccountRepository
) {
    fun execute(input: DepositInput) {
        val accountAsset = AccountAsset(UUID.fromString(input.accountId), input.assetId, input.quantity)
        accountRepository.saveAccountAsset(accountAsset)
    }
}

@Serializable
data class DepositInput(
    val accountId: String,
    val assetId: String,
    val quantity: Int
)