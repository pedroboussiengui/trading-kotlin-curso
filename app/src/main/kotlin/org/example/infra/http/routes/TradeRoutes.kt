package org.example.infra.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.application.usecase.GetTrades
import org.example.application.usecase.GetTradesOutput

fun Route.tradeRoutes(
    getTrades: GetTrades
) {
    get("/markets/trades") {
        try {
            val marketId = call.parameters["marketId"]
            val output: List<GetTradesOutput> = getTrades.execute(marketId!!)
            call.respond(output)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.NotFound,
                e.message!!
            )
        }
    }
}