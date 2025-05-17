package org.example

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotliquery.sessionOf
import org.example.application.handler.OrderHandler
import org.example.application.usecase.*
import org.example.infra.database.PostgresDataSource
import org.example.infra.http.KtorAdapter
import org.example.infra.http.websocket.KtorWebSockerServer
import org.example.infra.mediator.Mediator
import org.example.infra.repository.AccountRepositoryDatabase
import org.example.infra.repository.OrderRepositoryDatabase
import org.example.infra.repository.TradeRepositoryDatabase

fun main() {
    val dataSource = PostgresDataSource.dataSource

    val session = sessionOf(dataSource)
    val webSockerServer = KtorWebSockerServer()

    val accountRepository = AccountRepositoryDatabase(session)
    val orderRepository = OrderRepositoryDatabase(session)
    val tradeRepository = TradeRepositoryDatabase(session)

    val mediator = Mediator()

    val signup = SignUp(accountRepository)
    val deposit = Deposit(accountRepository)
    val withdraw = WithDraw(accountRepository)
    val getAccount = GetAccount(accountRepository)
    val placeOrder = PlaceOrder(orderRepository, mediator)
    val executeOrder = ExecuteOrder(orderRepository, tradeRepository)
    val getOrder = GetOrder(orderRepository)
    val getDepth = GetDepth(orderRepository)
    val getTrades = GetTrades(tradeRepository)

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    OrderHandler.config(mediator, webSockerServer, executeOrder, getDepth, scope)

    KtorAdapter(
        signup,
        deposit,
        withdraw,
        getAccount,
        placeOrder,
        getOrder,
        getDepth,
        getTrades,
        webSockerServer
    ).start(port = 3000)
}
