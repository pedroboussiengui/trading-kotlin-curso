package org.example.infra.http

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.example.application.usecase.*
import org.example.infra.http.routes.accountRoutes
import org.example.infra.http.routes.orderRoutes
import org.example.infra.http.routes.tradeRoutes
import org.example.infra.http.websocket.WebSocketServer
import org.example.infra.http.websocket.websocketRouter
import java.time.Duration

class KtorAdapter(
    private val signup: SignUp,
    private val deposit: Deposit,
    private val withdraw: WithDraw,
    private val getAccount: GetAccount,
    private val placeOrder: PlaceOrder,
    private val getOrder: GetOrder,
    private val getDepth: GetDepth,
    private val getTrades: GetTrades,
    private val webSocketServer: WebSocketServer<DefaultWebSocketServerSession>
) {
    fun start(port: Int) {
        embeddedServer(CIO, port = port) {
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
                tradeRoutes(getTrades)
                websocketRouter(webSocketServer)
            }
        }.start(wait = true)
    }
}