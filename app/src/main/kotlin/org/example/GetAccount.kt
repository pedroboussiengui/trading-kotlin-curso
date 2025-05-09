package org.example

class GetAccount(
    val accountDAO: AccountDAO
) {
    fun execute(accountId: String): Account? {
        val account = accountDAO.getAccountById(accountId)
        val assets = accountDAO.getAccountAssets(accountId)
        for (asset in assets) {
            account?.assets?.add(asset)
        }
        return account
    }
}