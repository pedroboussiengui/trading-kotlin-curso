package org.example.unit

import org.example.domain.Email
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EmailTest {
    @ParameterizedTest(name = "Deve validar o email {0}")
    @ValueSource(strings = ["john.doe@gmail.com"])
    fun `Deve criar um novo email válido`(email: String) {
        Assertions.assertNotNull(Email(email))
    }

    @ParameterizedTest(name = "Deve validar o nome {0}")
    @ValueSource(strings = [
        "john.doe@",
        "john.doe"
    ])
    fun `Deve lançar uma exceção ao criar email`(email: String) {
        assertThrows<Exception> {
            Email(email)
        }.let { err ->
            Assertions.assertEquals("Invalid email", err.message)
        }
    }
}