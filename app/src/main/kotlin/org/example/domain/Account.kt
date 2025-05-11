package org.example.domain

import java.util.UUID

data class Account(
    val accountId: String,
    val name: String,
    val email: String,
    val document: String,
    val password: String
) {

    init {
        if (!isValidName(name)) { throw Exception("Invalid name") }
        if (!this.isValidEmail(email)) { throw Exception("Invalid email") }
        if (!isValidPassword(password)) { throw Exception("Invalid password") }
        if (!validateCpf(document)) { throw Exception("Invalid document") }
    }

    companion object {
        fun create(
            name: String,
            email: String,
            document: String,
            password: String
        ): Account {
            val accountId = UUID.randomUUID().toString()
            return Account(accountId, name, email, document, password)
        }
    }

    private fun isValidName(name: String): Boolean {
        return name.split(" ").size == 2
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        return emailRegex.matches(email)
    }
}