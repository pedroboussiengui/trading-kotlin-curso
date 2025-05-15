package org.example.unit

import org.example.domain.Name
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NameTest {

    @ParameterizedTest(name = "Deve validar o nome {0}")
    @ValueSource(strings = [
        "Ana Maria",
        "Pedro Henrique",
        "João Souza",
    ])
    fun `Deve criar um novo nome válido`(name: String) {
        Assertions.assertNotNull(Name(name))
    }

    @ParameterizedTest(name = "Deve validar o nome {0}")
    @ValueSource(strings = [
        "Ana",
        "Pedro",
        "João",
        "-",
        "**",
        "",
    ])
    fun `Deve lançar uma exceção ao criar nome`(name: String) {
        assertThrows<Exception> {
            Name(name)
        }.let { err ->
            Assertions.assertEquals("Invalid name", err.message)
        }
    }
}