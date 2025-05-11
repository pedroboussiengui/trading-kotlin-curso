//package org.example

import kotliquery.queryOf
import kotliquery.sessionOf
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

//val dataSource: DataSource = SQLiteDataSource().apply {
//    url = "jdbc:sqlite:database.db"
//}
//val session = sessionOf(dataSource)

//fun saveAccount(account: Account) {
//    session.run(
//        queryOf("INSERT INTO account (account_id, name, email, document, password) VALUES (?, ?, ?, ?, ?)",
//            account.accountId, account.name, account.email, account.document, account.password).asUpdate
//    )
//}

//fun saveAccountAsset(accountAsset: Deposit) {
//    session.run(
//        queryOf("INSERT INTO account_asset (account_id, asset_id, quantity) VALUES (?, ?, ?)",
//            accountAsset.accountId, accountAsset.assetId, accountAsset.quantity).asUpdate
//    )
//}

//fun getAccountAsset(accountId: String, assetId: String): Asset? {
//    val accountAssetData = session.run(queryOf(
//        "SELECT * FROM account_asset WHERE account_id = ? and asset_id = ?", accountId, assetId)
//        .map { row ->
//            Asset(
//                row.string("asset_id"),
//                row.int("quantity"),
//            )
//        }.asSingle
//    )
//    return accountAssetData
//}

//fun updateAccountAsset(quantity: Int, accountId: String, assetId: String) {
//    session.run(
//        queryOf("UPDATE account_asset SET quantity = ? WHERE account_id = ? and asset_id = ?",
//            quantity, accountId, assetId).asUpdate
//    )
//}

//fun saveOrder(order: Order) {
//    session.run(
//        queryOf("INSERT INTO order_tb (order_id, market_id, account_id, side, quantity, price, status, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
//            order.orderId,
//            order.marketId,
//            order.accountId,
//            order.side,
//            order.quantity,
//            order.price,
//            order.status,
//            order.timestamp
//        ).asUpdate
//    )
//}

//fun getOrderById(orderId: String): Order? {
//    val order = session.run(queryOf(
//        "SELECT * FROM order_tb WHERE order_id = ?", orderId)
//        .map { row ->
//            Order(
//                row.string("order_id"),
//                row.string("market_id"),
//                row.string("account_id"),
//                row.string("side"),
//                row.int("quantity"),
//                row.int("price"),
//                row.string("status"),
//                row.string("timestamp")
//            )
//        }.asSingle
//    )
//    return order
//}

//fun getAccountById(accountId: String): Account? {
//    val account = session.run(queryOf(
//        "SELECT * FROM account WHERE account_id = ?", accountId)
//        .map { row ->
//            Account(
//                row.string("account_id"),
//                row.string("name"),
//                row.string("email"),
//                row.string("document"),
//                row.string("password")
//            )
//        }.asSingle
//    )
//    return account
//}
//
//fun getAccountAssets(accountId: String): List<Asset> {
//    val assets = session.run(queryOf(
//        "SELECT * FROM account_asset WHERE account_id = ?", accountId)
//        .map { row ->
//            Asset(
//                row.string("asset_id"),
//                row.int("quantity"),
//            )
//        }.asList
//    )
//    return assets
//}