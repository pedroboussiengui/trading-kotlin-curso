package org.example

import kotliquery.sessionOf
import org.example.application.handler.OrderHandler
import org.example.application.usecase.*
import org.example.infra.http.KtorAdapter
import org.example.infra.http.websocket.KtorWebSockerServer
import org.example.infra.mediator.Mediator
import org.example.infra.repository.AccountRepositoryDatabase
import org.example.infra.repository.OrderRepositoryDatabase
import org.example.infra.repository.TradeRepositoryDatabase
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

fun main() {

    val dataSource: DataSource = SQLiteDataSource().apply {
        url = "jdbc:sqlite:database.db"
    }
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

    OrderHandler.config(mediator, webSockerServer, executeOrder, getDepth)

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
