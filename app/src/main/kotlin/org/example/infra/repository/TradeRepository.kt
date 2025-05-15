package org.example.infra.repository

import kotliquery.Session
import kotliquery.queryOf
import org.example.domain.Trade

interface TradeRepository {
    fun saveTrade(trade: Trade)
    fun getTradesByMarketId(marketId: String): List<Trade>
}

const val INSERT_TRADE_QUERY = "INSERT INTO trade (trade_id, market_id, buy_order_id, sell_order_id, side, quantity, price, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
const val SELECT_TRADES_BY_MARKET_ID = "SELECT * FROM trade WHERE market_id = ?"

class TradeRepositoryDatabase(private val session: Session) : TradeRepository {

    override fun saveTrade(trade: Trade) {
        session.run(
            queryOf(INSERT_TRADE_QUERY.trim(),
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
        return session.run(queryOf(SELECT_TRADES_BY_MARKET_ID, marketId)
            .map { row ->
                Trade(
                    row.string("trade_id"),
                    row.string("market_id"),
                    row.string("buy_order_id"),
                    row.string("sell_order_id"),
                    row.string("side"),
                    row.int("quantity"),
                    row.int("price"),
                    row.string("timestamp"),
                )
            }.asList
        )
    }
}

