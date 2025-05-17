package org.example.infra.repository

import kotliquery.Session
import kotliquery.queryOf
import org.example.domain.Trade

interface TradeRepository {
    fun saveTrade(trade: Trade)
    fun getTradesByMarketId(marketId: String): List<Trade>
}

val InsertTradeQuery = """
    INSERT INTO ccca.trade (
        trade_id, market_id, buy_order_id, sell_order_id,
        side, quantity, price, timestamp
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
""".trimIndent()

val SelectTradesByMarketId = """
    SELECT * FROM ccca.trade 
    WHERE market_id = ?
""".trimIndent()

class TradeRepositoryDatabase(private val session: Session) : TradeRepository {

    override fun saveTrade(trade: Trade) {
        session.run(
            queryOf(InsertTradeQuery,
                trade.tradeId,
                trade.marketId,
                trade.buyOrderId,
                trade.sellOrderId,
                trade.side,
                trade.quantity,
                trade.price,
                trade.timestamp
            ).asUpdate
        )
    }

    override fun getTradesByMarketId(marketId: String): List<Trade> {
        return session.run(queryOf(SelectTradesByMarketId, marketId)
            .map { row ->
                Trade(
                    row.uuid("trade_id"),
                    row.string("market_id"),
                    row.uuid("buy_order_id"),
                    row.uuid("sell_order_id"),
                    row.string("side"),
                    row.int("quantity"),
                    row.double("price"),
                    row.instant("timestamp"),
                )
            }.asList
        )
    }
}

