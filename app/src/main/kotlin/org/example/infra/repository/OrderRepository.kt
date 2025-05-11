package org.example.infra.repository

import kotliquery.Session
import kotliquery.queryOf
import org.example.domain.Order

interface OrderRepository {
    fun saveOrder(order: Order)
    fun getOrderById(orderId: String): Order?
}

class OrderRepositoryDatabase(private val session: Session) : OrderRepository {

    override fun saveOrder(order: Order) {
        session.run(
            queryOf("INSERT INTO order_tb (order_id, market_id, account_id, side, quantity, price, status, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                order.orderId,
                order.marketId,
                order.accountId,
                order.side,
                order.quantity,
                order.price,
                order.status,
                order.timestamp
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
                    row.int("price"),
                    row.string("status"),
                    row.string("timestamp")
                )
            }.asSingle
        )
        return order
    }
}