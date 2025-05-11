package org.example

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

class KtorAdapter(
    private val signup: SignUp,
    private val deposit: Deposit,
    private val withdraw: WithDraw,
    private val getAccount: GetAccount,
    private val placeOrder: PlaceOrder,
    private val getOrder: GetOrder
) {
    fun start() {
        embeddedServer(CIO, port = 3000) {
            install(ContentNegotiation) {
                json()
            }
            routing {
                accountRoutes(signup, deposit, withdraw, getAccount)
                orderRoutes(placeOrder, getOrder)
            }
        }.start(wait = true)
    }
}