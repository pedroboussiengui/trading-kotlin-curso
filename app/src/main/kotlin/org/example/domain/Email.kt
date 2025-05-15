package org.example.domain

class Email(
    private val value: String
) {
    init {
        val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        if (!emailRegex.matches(value)) {
            throw Exception("Invalid email")
        }
    }

    fun getValue() = value
}
