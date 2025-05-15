package org.example.infra.http.routes

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.cancellation.CancellationException

class ConnectionManager {
    private val sessions = CopyOnWriteArrayList<DefaultWebSocketServerSession>()

    fun size() = sessions.size

    fun add(session: DefaultWebSocketServerSession) {
        sessions.add(session)
    }

    fun remove(session: DefaultWebSocketServerSession) {
        sessions.remove(session)
    }

    suspend fun broadcast(message: String) {
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