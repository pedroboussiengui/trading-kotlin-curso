package org.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotliquery.queryOf
import kotliquery.sessionOf
import org.sqlite.SQLiteDataSource
import java.time.LocalDate
import java.util.*
import javax.sql.DataSource

@Serializable
data class Account(
    val accountId: String? = null,
    val name: String,
    val email: String,
    val document: String,
    val password: String? = null,
    val assets: MutableList<Asset> = mutableListOf()
)

@Serializable
data class Asset(
    val assetId: String,
    val quantity: Int
)

@Serializable
data class Deposit(
    val accountId: String,
    val assetId: String,
    val quantity: Int
)

@Serializable
data class Withdraw(
    val accountId: String,
    val assetId: String,
    val quantity: Int
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class Order(
    val orderId: String? = null,
    val marketId: String,
    val accountId: String,
    val side: String,
    val quantity: Int,
    val price: Int,
    val status: String? = null,
    val timestamp: String? = null
)

fun isValidName(name: String): Boolean {
    return name.split(" ").size == 2
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    return emailRegex.matches(email)
}

fun main() {
    val dataSource: DataSource = SQLiteDataSource().apply {
        url = "jdbc:sqlite:database.db"
    }
    val session = sessionOf(dataSource)

    embeddedServer(CIO, port = 3000) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/signup") {
                val input = call.receive<Account>()
                if (!isValidName(input.name)) {
                    return@post call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse("Invalid name")
                    )
                }
                if (!isValidEmail(input.email)) {
                    return@post call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse("Invalid email")
                    )
                }
                if (!isValidPassword(input.password!!)) {
                    return@post call.respond(
                        HttpStatusCode.UnprocessableEntity,
                        ErrorResponse("Invalid password")
                    )
                }
                val newAccount = input.copy(accountId = UUID.randomUUID().toString())
                session.run(
                    queryOf("INSERT INTO account (account_id, name, email, document, password) VALUES (?, ?, ?, ?, ?)",
                        newAccount.accountId, newAccount.name, newAccount.email, newAccount.document, newAccount.password).asUpdate
                )
                call.respond(HttpStatusCode.Created, newAccount)
            }
            post("/deposit") {
                val input = call.receive<Deposit>()
                session.run(
                    queryOf("INSERT INTO account_asset (account_id, asset_id, quantity) VALUES (?, ?, ?)",
                        input.accountId, input.assetId, input.quantity).asUpdate
                )
            }
            post("/withdraw") {
                val input = call.receive<Withdraw>()
                val accountAssetData = session.run(queryOf(
                    "SELECT * FROM account_asset WHERE account_id = ? and asset_id = ?", input.accountId, input.assetId)
                    .map { row ->
                        Asset(
                            row.string("asset_id"),
                            row.int("quantity"),
                        )
                    }.asSingle
                )
                val currentQuantity = accountAssetData?.quantity
                if (accountAssetData == null || currentQuantity!! < input.quantity) {
                    call.respond(HttpStatusCode.UnprocessableEntity, ErrorResponse("Insufficient funds"))
                    return@post
                }
                val quantity = currentQuantity - input.quantity
                session.run(
                    queryOf("UPDATE account_asset SET quantity = ? WHERE account_id = ? and asset_id = ?",
                        quantity, input.accountId, input.assetId).asUpdate
                )
            }
            post("/place_order") {
                val input = call.receive<Order>()
                val newOrder = input.copy(orderId = UUID.randomUUID().toString(), status = "open", timestamp = LocalDate.now().toString())
                session.run(
                    queryOf("INSERT INTO order_tb (order_id, market_id, account_id, side, quantity, price, status, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        newOrder.orderId,
                        newOrder.marketId,
                        newOrder.accountId,
                        newOrder.side,
                        newOrder.quantity,
                        newOrder.price,
                        newOrder.status,
                        newOrder.timestamp
                    ).asUpdate
                )
                call.respond(newOrder)
            }
            get("/orders/{orderId}") {
                val orderId = call.parameters["orderId"]
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
                if (order != null) {
                    call.respond(order)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            get("/accounts/{accountId}") {
                val accountId = call.parameters["accountId"]
                val account = session.run(queryOf(
                    "SELECT * FROM account WHERE account_id = ?", accountId)
                    .map { row ->
                        Account(
                            row.string("account_id"),
                            row.string("name"),
                            row.string("email"),
                            row.string("document"),
                            row.string("password")
                        )
                    }.asSingle
                )
                val assets = session.run(queryOf(
                    "SELECT * FROM account_asset WHERE account_id = ?", accountId)
                    .map { row ->
                        Asset(
                            row.string("asset_id"),
                            row.int("quantity"),
                        )
                    }.asList
                )
                for (asset in assets) {
                    account?.assets?.add(asset)
                }
                if (account != null) {
                    call.respond(account)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }.start(wait = true)
}
