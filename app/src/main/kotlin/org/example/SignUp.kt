package org.example

import java.util.UUID

class SignUp(
    val accountDAO: AccountDAO
) {
    fun isValidName(name: String): Boolean {
        return name.split(" ").size == 2
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        return emailRegex.matches(email)
    }

    fun execute(input: Account): Account {
        if (!this.isValidName(input.name)) {
            throw Exception("Invalid name")
        }
        if (!this.isValidEmail(input.email)) {
            throw Exception("Invalid email")
        }
        if (!isValidPassword(input.password!!)) {
            throw Exception("Invalid password")
        }
        if (!validateCpf(input.document)) {
            throw Exception("Invalid document")
        }
        val account = input.copy(accountId = UUID.randomUUID().toString())
        accountDAO.saveAccount(account)
        return account
    }
}