package org.example.unit

import org.example.domain.isValidPassword
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Password validation")
class ValidatePasswordTest {

    @Test
    fun `should validate a password`() {
        val password = "asdQWE123"
        val isvalid = isValidPassword(password)
        Assertions.assertTrue(isvalid)
    }

    @ParameterizedTest(name = "Não deve validar a senha {0}")
    @ValueSource(
        strings = [
            "asd",
            "asdqwexzc",
            "ASDQWEXZC",
            "123456789"
        ])
    fun `should not validate a password`(password: String) {
        val isvalid = isValidPassword(password)
        Assertions.assertFalse(isvalid)
    }
}