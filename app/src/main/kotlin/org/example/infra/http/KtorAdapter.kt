package org.example.infra.http

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.example.application.usecase.Deposit
import org.example.application.usecase.GetAccount
import org.example.application.usecase.GetOrder
import org.example.application.usecase.PlaceOrder
import org.example.application.usecase.SignUp
import org.example.application.usecase.WithDraw
import org.example.infra.http.routes.accountRoutes
import org.example.infra.http.routes.orderRoutes

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