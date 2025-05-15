package org.example.domain

class Name(
    private val value: String
) {
    init {
        if (value.split(" ").size < 2) {
            throw Exception("Invalid name")
        }
    }

    fun getValue() = value
}


