package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.domain.Account
import org.example.infra.repository.AccountRepository

class SignUp(
    val accountRepository: AccountRepository
) {
    fun execute(input: AccountInput): SignupOutput {
        val account = Account.Companion.create(input.name, input.email, input.document, input.password)
        accountRepository.saveAccount(account)
        return SignupOutput(account.accountId.toString())
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