package org.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(CIO, port = 3000) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/signup") {
                try {
                    val input = call.receive<Account>()
                    val output = signup(input)
                    call.respond(HttpStatusCode.Created, output)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse(e.message!!)
                    )
                }
            }
            post("/deposit") {
                val input = call.receive<Deposit>()
                deposit(input)
            }
            post("/withdraw") {
                try {
                    val input = call.receive<Withdraw>()
                    withdraw(input)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse(e.message!!)
                    )
                }
            }
            post("/place_order") {
                val input = call.receive<Order>()
                val output = placeOrder(input)
                call.respond(output)
            }
            get("/orders/{orderId}") {
                val orderId = call.parameters["orderId"]
                val output = getOrder(orderId!!)
                if (output != null) {
                    call.respond(output)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            get("/accounts/{accountId}") {
                val accountId = call.parameters["accountId"]
                val output = getAccount(accountId!!)
                if (output != null) {
                    call.respond(output)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }.start(wait = true)
}
