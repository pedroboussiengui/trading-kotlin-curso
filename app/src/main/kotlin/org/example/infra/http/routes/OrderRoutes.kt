package org.example.infra.http.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.example.application.usecase.*

fun Route.orderRoutes(
    placeOrder: PlaceOrder,
    getOrder: GetOrder,
    getDepth: GetDepth
) {
    post("/place_order") {
        val input = call.receive<OrderInput>()
        val output: OrderOutput = placeOrder.execute(input)
        call.respond(output)
    }

    get("/orders/{orderId}") {
        try {
            val orderId: String by call.parameters
            val output: GetOrderOutput = getOrder.execute(orderId)
            call.respond(output)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.NotFound,
                e.message!!
            )
        }
    }

    get("/depth") {
        try {
            val marketId = call.parameters["marketId"]
            val output: GetDepthOutput = getDepth.execute(marketId!!, 0)
            call.respond(output)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.NotFound,
                e.message!!
            )
        }
    }
}