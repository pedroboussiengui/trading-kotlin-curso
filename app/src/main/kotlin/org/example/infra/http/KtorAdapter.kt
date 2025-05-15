package org.example.infra.http

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import org.example.application.usecase.*
import org.example.infra.http.routes.ConnectionManager
import org.example.infra.http.routes.accountRoutes
import org.example.infra.http.routes.orderRoutes
import java.time.Duration

class KtorAdapter(
    private val signup: SignUp,
    private val deposit: Deposit,
    private val withdraw: WithDraw,
    private val getAccount: GetAccount,
    private val placeOrder: PlaceOrder,
    private val getOrder: GetOrder,
    private val getDepth: GetDepth,
    private val connectionManager: ConnectionManager
) {
    fun start() {
        embeddedServer(CIO, port = 3000) {
            install(ContentNegotiation) {
                json()
            }
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(30)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            routing {
                accountRoutes(signup, deposit, withdraw, getAccount)
                orderRoutes(placeOrder, getOrder, getDepth)
                webSocket("/ws") {
                    println("new client")
                    connectionManager.add(this)
                    try {
                        incoming.consumeEach {  }
                    } catch (e: Exception) {
                        println("Session error: ${e.message}")
                    } finally {
                        println("Removing session")
                        connectionManager.remove(this)
                    }
                }
            }
        }.start(wait = true)
    }
}