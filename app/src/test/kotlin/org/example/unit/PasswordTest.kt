package org.example.unit

import org.example.domain.Password
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Password validation")
class PasswordTest {

    @ParameterizedTest(name = "Deve validar a senha {0}")
    @ValueSource(strings = ["asdQWE123"])
    fun `should validate a password`(password: String) {
        Assertions.assertNotNull(Password(password))
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
        assertThrows<Exception> {
            Password(password)
        }.let { err ->
            Assertions.assertEquals("Invalid password", err.message)
        }
    }
}