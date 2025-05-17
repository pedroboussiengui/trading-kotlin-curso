package org.example.infra.repository

import kotliquery.Session
import kotliquery.queryOf
import org.example.domain.Order
import java.util.*

interface OrderRepository {
    fun saveOrder(order: Order)
    fun updateOrder(order: Order)
    fun getOrderById(orderId: String): Order?
    fun getOrderByMarketIdAndStatus(marketId: String, status: String): List<Order>
    fun deleteAll()
}

val insertOrderQuery = """
    INSERT INTO ccca.order (
        order_id, market_id, account_id, side,
        quantity, price, status, timestamp,
        fill_quantity, fill_price
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""".trimIndent()

val updateOrderQuery = """
    UPDATE ccca.order 
    SET fill_quantity = ?, fill_price = ?, status = ? 
    WHERE order_id = ?
""".trimIndent()

val selectOrderByIdQuery = """
    SELECT * FROM ccca.order 
    WHERE order_id = ?
""".trimIndent()

val selectOrdersByMarketAndStatusQuery = """
    SELECT * FROM ccca.order 
    WHERE market_id = ? AND status = ?
""".trimIndent()

val deleteAllOrdersQuery = """
    DELETE FROM ccca.order
""".trimIndent()

class OrderRepositoryDatabase(private val session: Session) : OrderRepository {

    override fun saveOrder(order: Order) {
        session.run(
            queryOf(insertOrderQuery,
                order.orderId,
                order.marketId,
                order.accountId,
                order.side,
                order.quantity,
                order.price,
                order.status,
                order.timestamp,
                order.fillQuantity,
                order.price
            ).asUpdate
        )
    }

    override fun updateOrder(order: Order) {
        session.run(
            queryOf(updateOrderQuery,
                order.fillQuantity,
                order.fillPrice,
                order.status,
                order.orderId
            ).asUpdate
        )
    }

    override fun getOrderById(orderId: String): Order? {
        val order = session.run(queryOf(
            selectOrderByIdQuery, UUID.fromString(orderId))
            .map { row ->
                Order(
                    row.uuid("order_id"),
                    row.string("market_id"),
                    row.uuid("account_id"),
                    row.string("side"),
                    row.int("quantity"),
                    row.double("price"),
                    row.string("status"),
                    row.instant("timestamp"),
                    row.int("fill_quantity"),
                    row.double("fill_price")
                )
            }.asSingle
        )
        return order
    }

    override fun getOrderByMarketIdAndStatus(marketId: String, status: String): List<Order> {
        return session.run(queryOf(
            selectOrdersByMarketAndStatusQuery, marketId, status)
            .map { row ->
                Order(
                    row.uuid("order_id"),
                    row.string("market_id"),
                    row.uuid("account_id"),
                    row.string("side"),
                    row.int("quantity"),
                    row.double("price"),
                    row.string("status"),
                    row.instant("timestamp"),
                    row.int("fill_quantity"),
                    row.double("fill_price")
                )
            }.asList
        )
    }

    override fun deleteAll() {
        session.run(queryOf(deleteAllOrdersQuery).asUpdate)
    }
}