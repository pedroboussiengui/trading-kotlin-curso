package org.example

import kotliquery.queryOf

interface AccountDAO {
    fun saveAccount(account: Account)
    fun getAccountById(accountId: String): Account?
    fun getAccountAssets(accountId: String): List<Asset>
}

class AccountDAODatabase : AccountDAO {
    override fun saveAccount(account: Account) {
        session.run(
            queryOf("INSERT INTO account (account_id, name, email, document, password) VALUES (?, ?, ?, ?, ?)",
                account.accountId, account.name, account.email, account.document, account.password).asUpdate
        )
    }

    override fun getAccountById(accountId: String): Account? {
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
        return account
    }

    override fun getAccountAssets(accountId: String): List<Asset> {
        val assets = session.run(queryOf(
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
}