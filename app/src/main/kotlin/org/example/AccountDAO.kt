package org.example

import kotliquery.queryOf
import kotliquery.sessionOf
import org.sqlite.SQLiteDataSource
import javax.sql.DataSource

interface AccountDAO {
    fun saveAccount(account: Account)
    fun getAccountById(accountId: String): Account?
    fun getAccountAssets(accountId: String): List<Asset>
    fun getAccountAsset(accountId: String, assetId: String): Asset?
    fun updateAccountAsset(quantity: Int, accountId: String, assetId: String)
    fun saveAccountAsset(accountAsset: Deposit)
}

class AccountDAODatabase : AccountDAO {

    val dataSource: DataSource = SQLiteDataSource().apply {
        url = "jdbc:sqlite:database.db"
    }
    val session = sessionOf(dataSource)

    override fun saveAccount(account: Account) {
        this.session.run(
            queryOf("INSERT INTO account (account_id, name, email, document, password) VALUES (?, ?, ?, ?, ?)",
                account.accountId, account.name, account.email, account.document, account.password).asUpdate
        )
    }

    override fun getAccountById(accountId: String): Account? {
        val account = this.session.run(queryOf(
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
        return account
    }

    override fun getAccountAssets(accountId: String): List<Asset> {
        val assets = this.session.run(queryOf(
            "SELECT * FROM account_asset WHERE account_id = ?", accountId)
            .map { row ->
                Asset(
                    row.string("asset_id"),
                    row.int("quantity"),
                )
            }.asList
        )
        return assets
    }

    override fun getAccountAsset(accountId: String, assetId: String): Asset? {
        val accountAssetData = this.session.run(queryOf(
            "SELECT * FROM account_asset WHERE account_id = ? and asset_id = ?", accountId, assetId)
            .map { row ->
                Asset(
                    row.string("asset_id"),
                    row.int("quantity"),
                )
            }.asSingle
        )
        return accountAssetData
    }

    override fun updateAccountAsset(quantity: Int, accountId: String, assetId: String) {
        this.session.run(
            queryOf("UPDATE account_asset SET quantity = ? WHERE account_id = ? and asset_id = ?",
                quantity, accountId, assetId).asUpdate
        )
    }

    override fun saveAccountAsset(accountAsset: Deposit) {
        this.session.run(
            queryOf("INSERT INTO account_asset (account_id, asset_id, quantity) VALUES (?, ?, ?)",
                accountAsset.accountId, accountAsset.assetId, accountAsset.quantity).asUpdate
        )
    }
}

class AccountDAOMemory : AccountDAO {

    val accounts = mutableListOf<Account>()

    @Override
    override fun saveAccount(account: Account) {
        accounts.add(account)
    }

    override fun getAccountById(accountId: String): Account? {
        return accounts.find { it.accountId == accountId }
    }

    override fun getAccountAssets(accountId: String): List<Asset> {
        return listOf()
    }

    override fun getAccountAsset(accountId: String, assetId: String): Asset? {
        TODO("Not yet implemented")
    }

    override fun updateAccountAsset(quantity: Int, accountId: String, assetId: String) {
        TODO("Not yet implemented")
    }

    override fun saveAccountAsset(accountAsset: Deposit) {
        TODO("Not yet implemented")
    }
}