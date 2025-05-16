package org.example.infra.repository

import kotliquery.Session
import kotliquery.queryOf
import org.example.domain.Order
import java.time.LocalDateTime

interface OrderRepository {
    fun saveOrder(order: Order)
    fun updateOrder(order: Order)
    fun getOrderById(orderId: String): Order?
    fun getOrderByMarketIdAndStatus(marketId: String, status: String): List<Order>
    fun deleteAll()
}

class OrderRepositoryDatabase(private val session: Session) : OrderRepository {

    override fun saveOrder(order: Order) {
        session.run(
            queryOf("INSERT INTO order_tb (order_id, market_id, account_id, side, quantity, price, status, timestamp, fill_quantity, fill_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                order.orderId,
                order.marketId,
                order.accountId,
                order.side,
                order.quantity,
                order.price,
                order.status,
                order.timestamp.toString(),
                order.fillQuantity,
                order.price
            ).asUpdate
        )
    }

    override fun updateOrder(order: Order) {
        session.run(
            queryOf("UPDATE order_tb SET fill_quantity = ?, fill_price = ?, status = ? WHERE order_id = ?",
                order.fillQuantity,
                order.fillPrice,
                order.status,
                order.orderId
            ).asUpdate
        )
    }

    override fun getOrderById(orderId: String): Order? {
        val order = session.run(queryOf(
            "SELECT * FROM order_tb WHERE order_id = ?", orderId)
            .map { row ->
                Order(
                    row.string("order_id"),
                    row.string("market_id"),
                    row.string("account_id"),
                    row.string("side"),
                    row.int("quantity"),
                    row.double("price"),
                    row.string("status"),
                    LocalDateTime.parse(row.string("timestamp")),
                    row.int("fill_quantity"),
                    row.double("fill_price")
                )
            }.asSingle
        )
        return order
    }

    override fun getOrderByMarketIdAndStatus(marketId: String, status: String): List<Order> {
        return session.run(queryOf(
            "SELECT * FROM order_tb WHERE market_id = ? and status = ?", marketId, status)
            .map { row ->
                Order(
                    row.string("order_id"),
                    row.string("market_id"),
                    row.string("account_id"),
                    row.string("side"),
                    row.int("quantity"),
                    row.double("price"),
                    row.string("status"),
                    LocalDateTime.parse(row.string("timestamp")),
                    row.int("fill_quantity"),
                    row.double("fill_price")
                )
            }.asList
        )
    }

    override fun deleteAll() {
        session.run(queryOf("DELETE FROM order_tb").asUpdate)
    }
}