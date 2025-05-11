package org.example.infra.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.example.application.usecase.GetOrder
import org.example.application.usecase.GetOrderOutput
import org.example.application.usecase.OrderInput
import org.example.application.usecase.OrderOutput
import org.example.application.usecase.PlaceOrder

fun Route.orderRoutes(
    placeOrder: PlaceOrder,
    getOrder: GetOrder
) {
    post("/place_order") {
        val input = call.receive<OrderInput>()
        val output: OrderOutput = placeOrder.execute(input)
        call.respond(output)
    }

    get("/orders/{orderId}") {
        try {
            val orderId = call.parameters["orderId"]
            val output: GetOrderOutput = getOrder.execute(orderId!!)
            call.respond(output)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.NotFound,
                e.message!!
            )
        }
    }
}