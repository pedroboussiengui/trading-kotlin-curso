package org.example

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotliquery.sessionOf
import org.example.application.handler.OrderHandler
import org.example.application.usecase.*
import org.example.infra.http.KtorAdapter
//import org.example.infra.http.websocket.ConnectionManager
import org.example.infra.http.websocket.KtorWebSockerServer
import org.example.infra.mediator.Mediator
import org.example.infra.repository.AccountRepositoryDatabase
import org.example.infra.repository.OrderRepositoryDatabase
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

    val mediator = Mediator()

    val signup = SignUp(accountRepository)
    val deposit = Deposit(accountRepository)
    val withdraw = WithDraw(accountRepository)
    val getAccount = GetAccount(accountRepository)
    val placeOrder = PlaceOrder(orderRepository, mediator)
    val executeOrder = ExecuteOrder(orderRepository)
    val getOrder = GetOrder(orderRepository)
    val getDepth = GetDepth(orderRepository)

    OrderHandler.config(mediator, webSockerServer, executeOrder, getDepth)

    KtorAdapter(
        signup,
        deposit,
        withdraw,
        getAccount,
        placeOrder,
        getOrder,
        getDepth,
        webSockerServer
    ).start()
}
