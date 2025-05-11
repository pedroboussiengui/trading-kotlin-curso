package org.example

import kotlinx.serialization.Serializable

class GetAccount(
    val accountRepository: AccountRepository
) {
    fun execute(accountId: String): GetAccountOutput {
        val account = accountRepository.getAccountById(accountId)
        if (account == null) {
            throw Exception("Account not found")
        }
        val assets = accountRepository.getAccountAssets(accountId)
        val output = GetAccountOutput(
            account.accountId,
            account.name,
            account.email,
            account.document,
            account.password,
            assets.map { GetAccountOutput.Asset(it.assetId, it.getQuantity()) }.toMutableList()
        )
        return output
    }
}

@Serializable
data class GetAccountOutput (
    val accountId: String,
    val name: String,
    val email: String,
    val document: String,
    val password: String,
    val assets: MutableList<Asset> = mutableListOf()
) {
    @Serializable
    data class Asset(
        val assetId: String,
        val quantity: Int
    )
}