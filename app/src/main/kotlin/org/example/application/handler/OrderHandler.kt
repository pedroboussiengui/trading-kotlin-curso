package org.example.application.handler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.example.application.usecase.ExecuteOrder
import org.example.application.usecase.GetDepth
import org.example.infra.http.websocket.WebSocketServer
import org.example.infra.mediator.Mediator

class OrderHandler{

    companion object {
        fun config(
            mediator: Mediator,
            webSockerServer: WebSocketServer<*>,
            executeOrder: ExecuteOrder,
            getDepth: GetDepth,
            coroutineScope: CoroutineScope
        ) {
            mediator.register<String, Unit>("orderPlaced") { marketId ->
                println("order placed $marketId")
                executeOrder.execute(marketId)
                val depth = getDepth.execute(marketId, 0)
                val depthJson = Json.encodeToString(depth)
                coroutineScope.launch {
                    webSockerServer.broadcast(depthJson)
                }
            }
        }
    }
}