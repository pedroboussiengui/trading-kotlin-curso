package org.example.infra.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object PostgresDataSource {
    val dataSource: HikariDataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/app"
            username = "postgres"
            password = "123456"
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
        }
        HikariDataSource(config)
    }
}