package org.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

/**
 * É como se fosse um Controller, mas aqui usarei o nome routing
 * pra manter a consistencia com o Ktor
 */
fun Route.accountRoutes(
    signup: SignUp,
    deposit: Deposit,
    withdraw: WithDraw,
    getAccount: GetAccount
) {
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