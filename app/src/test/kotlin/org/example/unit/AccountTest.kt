package org.example.unit

import org.example.domain.Account
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AccountTest {

    @Test
    fun `deve criar uma conta`() {
        val account = Account.create(
            "John Doe",
            "john.doe@gmail.com",
            "97456321558",
            "asdQWE123"
        )
        Assertions.assertNotNull(account)
    }

    @Test
    fun `não deve criar uma conta com nome inválido`() {
        val exception = assertThrows<Exception> {
            Account.create(
                "John",
                "john.doe@gmail.com",
                "97456321558",
                "asdQWE123"
            )
        }
        Assertions.assertEquals("Invalid name", exception.message)
    }

    @Test
    fun `não deve criar uma conta com email inválido`() {
        val exception = assertThrows<Exception> {
            Account.create(
                "John Doe",
                "john.doegmail.com",
                "97456321558",
                "asdQWE123"
            )
        }
        Assertions.assertEquals("Invalid email", exception.message)
    }

    @Test
    fun `não deve criar uma conta com documento inválido`() {
        val exception = assertThrows<Exception> {
            Account.create(
                "John Doe",
                "john.doe@gmail.com",
                "974563215",
                "asdQWE123"
            )
        }
        Assertions.assertEquals("Invalid document", exception.message)
    }

    /**
     * As varias possibilidades de senha invalida já estão sendo testadas separadamente
     * em outro arquivo de teste, aqui é somente um caso ínválido
     */
    @Test
    fun `não deve criar uma conta com senha inválido`() {
        val exception = assertThrows<Exception> {
            Account.create(
                "John Doe",
                "john.doe@gmail.com",
                "97456321558",
                "asdQWE"
            )
        }
        Assertions.assertEquals("Invalid password", exception.message)
    }

}