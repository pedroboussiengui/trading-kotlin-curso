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

    val accountRepository = AccountRepositoryDatabase()
    val orderRepository = OrderRepositoryDatabase()

    val signup = SignUp(accountRepository)
    val getAccount = GetAccount(accountRepository)
    val withdraw = WithDraw(accountRepository)
    val deposit = Deposit(accountRepository)
    val placeOrder = PlaceOrder(orderRepository)
    val getOrder = GetOrder(orderRepository)

    embeddedServer(CIO, port = 3000) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/signup") {
                try {
                    val input = call.receive<AccountInput>()
                    val output: SignupOutput = signup.execute(input)
                    call.respond(HttpStatusCode.Created, output)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse(e.message!!)
                    )
                }
            }
            post("/deposit") {
                val input = call.receive<DepositInput>()
                deposit.execute(input)
            }
            post("/withdraw") {
                try {
                    val input = call.receive<WithDrawInput>()
                    withdraw.execute(input)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse(e.message!!)
                    )
                }
            }
            post("/place_order") {
                val input = call.receive<OrderInput>()
                val output: OrderOutput = placeOrder.execute(input)
                call.respond(output)
            }
            get("/orders/{orderId}") {
                try {
                    val orderId = call.parameters["orderId"]
                    val output: GetOrderOutput = getOrder.execute(orderId!!)
                    call.respond(output)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        e.message!!
                    )
                }
            }
            get("/accounts/{accountId}") {
                try {
                    val accountId = call.parameters["accountId"]
                    val output: GetAccountOutput = getAccount.execute(accountId!!)
                    call.respond(output)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        e.message!!
                    )
                }
            }
        }
    }.start(wait = true)
}
