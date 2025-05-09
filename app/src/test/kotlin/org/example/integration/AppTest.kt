package org.example.integration

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.example.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AppTest {
    val client = HttpClient(CIO) {
        install(ContentNegotiation.Plugin) {
            json(Json { ignoreUnknownKeys = true })
        }
        expectSuccess = false // semelhante ao validateStatus = () => true
    }

    @Test
    fun `deve criar um conta válida`() = runBlocking {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<Account>()
        Assertions.assertNotNull(outputSignup.accountId)

        val responseGet = client.get("http://localhost:3000/accounts/${outputSignup.accountId}")
        val outputGet = responseGet.body<Account>()

        Assertions.assertEquals(inputSignup.name, outputGet.name)
        Assertions.assertEquals(inputSignup.email, outputGet.email)
        Assertions.assertEquals(inputSignup.document, outputGet.document)
    }

    @Test
    fun `não deve criar uma conta com nome inválido`() = runBlocking {
        val inputSignup = Account(
            name = "John",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<ErrorResponse>()
        Assertions.assertEquals(HttpStatusCode.Companion.UnprocessableEntity, responseSignup.status)
        Assertions.assertEquals("Invalid name", outputSignup.error)
    }

//    @Test
//    fun `não deve criar uma conta com email inválido`() = runBlocking {
//        val inputSignup = Account(
//            name = "John Doe",
//            email = "john.doe",
//            document = "97456321558",
//            password = "asdQWE123"
//        )
//        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
//            contentType(ContentType.Application.Json)
//            setBody(inputSignup)
//        }
//        val outputSignup = responseSignup.body<ErrorResponse>()
//        Assertions.assertEquals(HttpStatusCode.Companion.UnprocessableEntity, responseSignup.status)
//        Assertions.assertEquals("Invalid email", outputSignup.error)
//    }
//
//    @Test
//    fun `não deve criar uma conta com senha inválido`() = runBlocking {
//        val inputSignup = Account(
//            name = "John Doe",
//            email = "john.doe@gmail.com",
//            document = "97456321558",
//            password = "asdQWE"
//        )
//        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
//            contentType(ContentType.Application.Json)
//            setBody(inputSignup)
//        }
//        val outputSignup = responseSignup.body<ErrorResponse>()
//        Assertions.assertEquals(HttpStatusCode.Companion.UnprocessableEntity, responseSignup.status)
//        Assertions.assertEquals("Invalid password", outputSignup.error)
//    }

    @Test
    fun `deve fazer um deposito`() = runBlocking {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<Account>()
        val inputDeposit = Deposit(
            accountId = outputSignup.accountId!!,
            assetId = "BTC/USD",
            quantity = 10
        )
        client.post("http://localhost:3000/deposit") {
            contentType(ContentType.Application.Json)
            setBody(inputDeposit)
        }
        val responseGetAccount = client.get("http://localhost:3000/accounts/${outputSignup.accountId}")
        val outputGetAccount = responseGetAccount.body<Account>()
        Assertions.assertEquals(1, outputGetAccount.assets.size)
        Assertions.assertEquals("BTC/USD", outputGetAccount.assets[0].assetId)
        Assertions.assertEquals(10, outputGetAccount.assets[0].quantity)
    }

    @Test
    fun `deve fazer um saque`() = runBlocking {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<Account>()
        val inputDeposit = Deposit(
            accountId = outputSignup.accountId!!,
            assetId = "BTC/USD",
            quantity = 10
        )
        client.post("http://localhost:3000/deposit") {
            contentType(ContentType.Application.Json)
            setBody(inputDeposit)
        }
        val inputWithdraw = Withdraw(
            accountId = outputSignup.accountId,
            assetId = "BTC/USD",
            quantity = 5
        )
        client.post("http://localhost:3000/withdraw") {
            contentType(ContentType.Application.Json)
            setBody(inputWithdraw)
        }
        val responseGetAccount = client.get("http://localhost:3000/accounts/${outputSignup.accountId}")
        val outputGetAccount = responseGetAccount.body<Account>()
        Assertions.assertEquals(1, outputGetAccount.assets.size)
        Assertions.assertEquals("BTC/USD", outputGetAccount.assets[0].assetId)
        Assertions.assertEquals(5, outputGetAccount.assets[0].quantity)
    }

    @Test
    fun `não deve fazer um saque quando sem fundos`() = runBlocking {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<Account>()
        val inputDeposit = Deposit(
            accountId = outputSignup.accountId!!,
            assetId = "BTC/USD",
            quantity = 5
        )
        client.post("http://localhost:3000/deposit") {
            contentType(ContentType.Application.Json)
            setBody(inputDeposit)
        }
        val inputWithdraw = Withdraw(
            accountId = outputSignup.accountId,
            assetId = "BTC/USD",
            quantity = 10
        )
        val responseWithdraw: HttpResponse = client.post("http://localhost:3000/withdraw") {
            contentType(ContentType.Application.Json)
            setBody(inputWithdraw)
        }
        val outputWithdraw = responseWithdraw.body<ErrorResponse>()
        Assertions.assertEquals(HttpStatusCode.Companion.UnprocessableEntity, responseWithdraw.status)
        Assertions.assertEquals("Insufficient funds", outputWithdraw.error)
    }

    @Test
    fun `deve criar uma ordem de venda`() = runBlocking {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<Account>()
        val inputPlaceOrder = Order(
            marketId = "BTC/USD",
            accountId = outputSignup.accountId!!,
            side = "sell",
            quantity = 1,
            price = 94000
        )
        val responsePlaceOrder: HttpResponse = client.post("http://localhost:3000/place_order") {
            contentType(ContentType.Application.Json)
            setBody(inputPlaceOrder)
        }
        val outputPlaceOrder = responsePlaceOrder.body<Order>()
        Assertions.assertNotNull(outputPlaceOrder.orderId)
        val responseGetOrder = client.get("http://localhost:3000/orders/${outputPlaceOrder.orderId}")
        val outputGetOrder = responseGetOrder.body<Order>()
        Assertions.assertEquals("BTC/USD", outputGetOrder.marketId)
        Assertions.assertEquals("sell", outputGetOrder.side)
        Assertions.assertEquals(1, outputGetOrder.quantity)
        Assertions.assertEquals(94000, outputGetOrder.price)
        Assertions.assertNotNull(outputGetOrder.timestamp)
    }
}