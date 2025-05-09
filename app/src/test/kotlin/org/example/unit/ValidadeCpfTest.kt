package org.example.unit

import org.example.validateCpf
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Validação de CPF")
class ValidadeCpfTest {

    @ParameterizedTest(name = "Deve validar o cpf {0}")
    @ValueSource(strings = [
        "97456321558",
        "71428793860",
        "87748248800",
        "877.482.488-00"
    ])
    fun `Deve validar o cpf`(cpf: String) {
        val isValid = validateCpf(cpf)
        Assertions.assertTrue(isValid)
    }

    @ParameterizedTest(name = "Não deve validar o cpf {0}")
    @ValueSource(strings = [
        "11111111111",
        "111",
        "abc",
        "12345678900",
        ""
    ])
    fun `Não deve validar o cpf`(cpf: String) {
        val isValid = validateCpf(cpf)
        Assertions.assertFalse(isValid)
    }
}