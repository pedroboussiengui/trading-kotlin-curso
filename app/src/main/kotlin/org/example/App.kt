package org.example

import kotliquery.sessionOf
import org.example.application.usecase.Deposit
import org.example.application.usecase.GetAccount
import org.example.application.usecase.GetDepth
import org.example.application.usecase.GetOrder
import org.example.application.usecase.PlaceOrder
import org.example.application.usecase.SignUp
import org.example.application.usecase.WithDraw
import org.example.infra.http.KtorAdapter
import org.example.infra.repository.AccountRepositoryDatabase
import org.example.infra.repository.OrderRepositoryDatabase
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

fun main() {

    val dataSource: DataSource = SQLiteDataSource().apply {
        url = "jdbc:sqlite:database.db"
    }
    val session = sessionOf(dataSource)

    val accountRepository = AccountRepositoryDatabase(session)
    val orderRepository = OrderRepositoryDatabase(session)

    val signup = SignUp(accountRepository)
    val deposit = Deposit(accountRepository)
    val withdraw = WithDraw(accountRepository)
    val getAccount = GetAccount(accountRepository)
    val placeOrder = PlaceOrder(orderRepository)
    val getOrder = GetOrder(orderRepository)
    val getDepth = GetDepth(orderRepository)

    KtorAdapter(
        signup,
        deposit,
        withdraw,
        getAccount,
        placeOrder,
        getOrder,
        getDepth
    ).start()
}
