package org.example.infra.repository

import kotliquery.Session
import kotliquery.queryOf
import org.example.domain.Account
import org.example.domain.AccountAsset
import java.util.UUID

interface AccountRepository {
    fun saveAccount(account: Account)
    fun getAccountById(accountId: String): Account?
    fun getAccountAssets(accountId: String): List<AccountAsset>
    fun getAccountAsset(accountId: String, assetId: String): AccountAsset
    fun updateAccountAsset(accountAsset: AccountAsset)
    fun saveAccountAsset(accountAsset: AccountAsset)
}

class AccountRepositoryDatabase(private val session: Session) : AccountRepository {

    override fun saveAccount(account: Account) {
        session.run(
            queryOf("INSERT INTO ccca.account (account_id, name, email, document, password) VALUES (?, ?, ?, ?, ?)",
                account.accountId, account.name, account.email, account.document, account.password).asUpdate
        )
    }

    override fun getAccountById(accountId: String): Account? {
        val account = session.run(queryOf(
            "SELECT * FROM ccca.account WHERE account_id = ?", UUID.fromString(accountId))
            .map { row ->
                Account(
                    row.uuid("account_id"),
                    row.string("name"),
                    row.string("email"),
                    row.string("document"),
                    row.string("password")
                )
            }.asSingle
        )
        return account
    }

    override fun getAccountAssets(accountId: String): List<AccountAsset> {
        val assets = session.run(queryOf(
            "SELECT * FROM ccca.account_asset WHERE account_id = ?", UUID.fromString(accountId))
            .map { row ->
                AccountAsset(
                    row.uuid("account_id"),
                    row.string("asset_id"),
                    row.int("quantity"),
                )
            }.asList
        )
        return assets
    }

    override fun getAccountAsset(accountId: String, assetId: String): AccountAsset {
        val accountAssetData = session.run(queryOf(
            "SELECT * FROM ccca.account_asset WHERE account_id = ? AND asset_id = ?", UUID.fromString(accountId), assetId)
            .map { row ->
                AccountAsset(
                    row.uuid("account_id"),
                    row.string("asset_id"),
                    row.int("quantity"),
                )
            }.asSingle
        )
        if (accountAssetData == null) {
            throw Exception("Asset not found")
        }
        return accountAssetData
    }

    override fun updateAccountAsset(accountAsset: AccountAsset) {
        session.run(
            queryOf("UPDATE ccca.account_asset SET quantity = ? WHERE account_id = ? AND asset_id = ?",
                accountAsset.getQuantity(), accountAsset.accountId, accountAsset.assetId).asUpdate
        )
    }

    override fun saveAccountAsset(accountAsset: AccountAsset) {
        session.run(
            queryOf("INSERT INTO ccca.account_asset (account_id, asset_id, quantity) VALUES (?, ?, ?)",
                accountAsset.accountId, accountAsset.assetId, accountAsset.getQuantity()).asUpdate
        )
    }
}

class AccountDAOMemory : AccountRepository {

    val accounts = mutableListOf<Account>()

    @Override
    override fun saveAccount(account: Account) {
        accounts.add(account)
    }

    override fun getAccountById(accountId: String): Account? {
        return accounts.find { it.accountId == UUID.fromString(accountId) }
    }

    override fun getAccountAssets(accountId: String): List<AccountAsset> {
        return listOf()
    }

    override fun getAccountAsset(accountId: String, assetId: String): AccountAsset {
        TODO("Not yet implemented")
    }

    override fun updateAccountAsset(accountAsset: AccountAsset) {
        TODO("Not yet implemented")
    }

    override fun saveAccountAsset(accountAsset: AccountAsset) {
        TODO("Not yet implemented")
    }
}