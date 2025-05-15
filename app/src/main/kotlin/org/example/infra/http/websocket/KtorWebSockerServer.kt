package org.example.infra.http.websocket

import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.cancellation.CancellationException

interface WebSocketServer<T> {
    fun add(session: T)
    fun remove(session: T)
    suspend fun broadcast(message: String)
}

class KtorWebSockerServer : WebSocketServer<DefaultWebSocketServerSession> {
    private val sessions = CopyOnWriteArrayList<DefaultWebSocketServerSession>()

    fun size() = sessions.size

    override fun add(session: DefaultWebSocketServerSession) {
        sessions.add(session)
    }

    override fun remove(session: DefaultWebSocketServerSession) {
        sessions.remove(session)
    }

    override suspend fun broadcast(message: String) {
        val iterator = sessions.iterator()
        while (iterator.hasNext()) {
            val session = iterator.next()
            try {
                session.send(Frame.Text(message))
            } catch (e: CancellationException) {
                println("Sessão cancelada: removendo da lista - ${e.message}")
                iterator.remove()
            } catch (e: Exception) {
                println("Erro ao enviar mensagem para sessão: ${e.message}")
                iterator.remove()
            }
        }
    }
}

fun Route.websocketRouter(webSocketServer: WebSocketServer<DefaultWebSocketServerSession>) {
    webSocket("/ws") {
        println("new client")
        webSocketServer.add(this)
        try {
            incoming.consumeEach {  }
        } catch (e: Exception) {
            println("Session error: ${e.message}")
        } finally {
            println("Removing session")
            webSocketServer.remove(this)
        }
    }
}