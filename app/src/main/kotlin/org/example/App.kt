package org.example

import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import java.util.UUID
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.cio.CIO

@Serializable
data class Account(
    val accountId: String? = null,
    val name: String,
    val email: String,
    val document: String,
    val password: String? = null
)

@Serializable
data class ErrorResponse(
    val error: String
)

fun isValidName(name: String): Boolean {
    return name.split(" ").size == 2
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    return emailRegex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false
    if (!Regex(".*\\d.*").containsMatchIn(password)) return false
    if (!Regex(".*[a-z].*").containsMatchIn(password)) return false
    if (!Regex(".*[A-Z].*").containsMatchIn(password)) return false
    return true
}

fun main() {
    val accounts = mutableListOf<Account>()
    embeddedServer(CIO, port = 3000) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/signup") {
                val input = call.receive<Account>()
                if (!isValidName(input.name)) {
                    return@post call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse("Invalid name")
                    )
                }
                if (!isValidEmail(input.email)) {
                    return@post call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse("Invalid email")
                    )
                }
                if (!isValidPassword(input.password!!)) {
                    return@post call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse("Invalid password")
                    )
                }
                println(input)
                val newAccount = input.copy(accountId = UUID.randomUUID().toString())
                accounts.add(newAccount)
                call.respond(HttpStatusCode.Created, newAccount)
            }
            get("/accounts/{accountId}") {
                val accountId = call.parameters["accountId"]
                val account = accounts.find { it.accountId == accountId }
                if (account != null) {
                    call.respond(account)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }.start(wait = true)
}
