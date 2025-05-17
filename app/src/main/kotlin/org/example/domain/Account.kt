package org.example.domain

import java.util.UUID

data class Account(
    val accountId: UUID,
    private val nameInput: String,
    private val emailInput: String,
    private val documentInput: String,
    private val passwordInput: String
) {
    init {
        Name(nameInput)
        Email(emailInput)
        Document(documentInput)
        Password(passwordInput)
    }

    val name: String
        get() = Name(nameInput).getValue()
    val email: String
        get() = Email(emailInput).getValue()
    val document: String
        get() = Document(documentInput).getValue()
    val password: String
        get() = Password(passwordInput).getValue()

    companion object {
        fun create(
            name: String,
            email: String,
            document: String,
            password: String
        ): Account {
            val accountId = UUID.randomUUID()
            return Account(accountId, name, email, document, password)
        }
    }
}