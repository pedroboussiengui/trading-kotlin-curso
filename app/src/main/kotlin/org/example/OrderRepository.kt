package org.example

import kotliquery.queryOf
import kotliquery.sessionOf
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

interface OrderRepository {
    fun saveOrder(order: Order)
    fun getOrderById(orderId: String): Order?
}

class OrderRepositoryDatabase : OrderRepository {
    val dataSource: DataSource = SQLiteDataSource().apply {
        url = "jdbc:sqlite:database.db"
    }
    val session = sessionOf(dataSource)

    override fun saveOrder(order: Order) {
        this.session.run(
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
        val order = this.session.run(queryOf(
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