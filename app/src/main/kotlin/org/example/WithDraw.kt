package org.example

class WithDraw(
    val accountDAO: AccountDAO
) {
    fun execute(input: Withdraw) {
        val accountAssetData = accountDAO.getAccountAsset(input.accountId, input.assetId)
        val currentQuantity = accountAssetData?.quantity
        if (accountAssetData == null || currentQuantity!! < input.quantity) {
            throw Exception("Insufficient funds")
        }
        val quantity = currentQuantity - input.quantity
        accountDAO.updateAccountAsset(quantity, input.accountId, input.assetId)
    }
}