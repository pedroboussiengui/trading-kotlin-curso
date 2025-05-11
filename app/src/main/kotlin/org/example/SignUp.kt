package org.example

import kotlinx.serialization.Serializable

class SignUp(
    val accountRepository: AccountRepository
) {
    fun execute(input: AccountInput): SignupOutput {
        val account = Account.create(input.name, input.email, input.document, input.password)
        accountRepository.saveAccount(account)
        return SignupOutput(account.accountId)
    }
}

@Serializable
data class AccountInput(
    val name: String,
    val email: String,
    val document: String,
    val password: String,
)

@Serializable
data class SignupOutput(
    val accountId: String
)