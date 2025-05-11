package org.example

class DepositUC(
    val accountDAO: AccountDAO
) {
    fun execute(input: Deposit) {
        accountDAO.saveAccountAsset(input)
    }
}