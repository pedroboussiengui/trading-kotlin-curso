package org.example.domain

class Password(
    private val value: String
) {
    init {
        if (!validate(value)) {
            throw Exception("Invalid password")
        }
    }

    fun getValue() = value

    private fun validate(password: String): Boolean {
        if (password.length < 8) return false
        if (!Regex(".*\\d.*").containsMatchIn(password)) return false
        if (!Regex(".*[a-z].*").containsMatchIn(password)) return false
        if (!Regex(".*[A-Z].*").containsMatchIn(password)) return false
        return true
    }
}
