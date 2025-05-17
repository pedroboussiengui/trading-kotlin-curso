package org.example.application.usecase

import kotlinx.serialization.Serializable
import org.example.infra.repository.TradeRepository

class GetTrades(
    val tradeRepository: TradeRepository
) {
    fun execute(marketId: String): List<GetTradesOutput> {
        return tradeRepository.getTradesByMarketId(marketId).map {
            GetTradesOutput(
                tradeId = it.tradeId.toString(),
                marketId = it.marketId,
                buyOrderId = it.buyOrderId.toString(),
                sellOrderId = it.sellOrderId.toString(),
                side = it.side,
                quantity = it.quantity,
                price = it.price,
                timestamp = it.timestamp.toString()
            )
        }
    }
}

@Serializable
data class GetTradesOutput(
    val tradeId: String,
    val marketId: String,
    val buyOrderId: String,
    val sellOrderId: String,
    val side: String,
    val quantity: Int,
    val price: Double,
    val timestamp: String
)