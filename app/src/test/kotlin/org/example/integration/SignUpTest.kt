package org.example.integration

import org.example.Account
import org.example.getAccountById
import org.example.signup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// teste de integração no nivel mais baixo
class SignUpTest {

    @Test
    fun `deve criar um conta válida`() {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val outputSignup = signup(inputSignup)
        Assertions.assertNotNull(outputSignup.accountId)
        val outputGet = getAccountById(outputSignup.accountId!!)!!
        Assertions.assertEquals(inputSignup.name, outputGet.name)
        Assertions.assertEquals(inputSignup.email, outputGet.email)
        Assertions.assertEquals(inputSignup.document, outputGet.document)
    }

    @Test
    fun `não deve criar um conta com um nome inválido`() {
        val inputSignup = Account(
            name = "John",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val exception = assertThrows<Exception> {
            signup(inputSignup)
        }
        Assertions.assertEquals("Invalid name", exception.message)
    }

    @Test
    fun `não deve criar um conta com um email inválido`() {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe",
            document = "97456321558",
            password = "asdQWE123"
        )
        val exception = assertThrows<Exception> {
            signup(inputSignup)
        }
        Assertions.assertEquals("Invalid email", exception.message)
    }

    @Test
    fun `não deve criar um conta com um documeno inválido`() {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "974563215",
            password = "asdQWE123"
        )
        val exception = assertThrows<Exception> {
            signup(inputSignup)
        }
        Assertions.assertEquals("Invalid document", exception.message)
    }

    @Test
    fun `não deve criar um conta com uma senha inválida`() {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "974563215",
            password = "asdQWE"
        )
        val exception = assertThrows<Exception> {
            signup(inputSignup)
        }
        Assertions.assertEquals("Invalid password", exception.message)
    }
}