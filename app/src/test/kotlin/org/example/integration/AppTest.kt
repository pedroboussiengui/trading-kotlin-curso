package org.example.integration

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import org.example.application.usecase.AccountInput
import org.example.application.usecase.DepositInput
import org.example.application.usecase.GetAccountOutput
import org.example.application.usecase.GetDepthOutput
import org.example.application.usecase.GetOrderOutput
import org.example.application.usecase.GetTradesOutput
import org.example.application.usecase.OrderInput
import org.example.application.usecase.OrderOutput
import org.example.application.usecase.SignupOutput
import org.example.application.usecase.WithDrawInput
import org.example.domain.Trade
import org.example.infra.http.routes.ErrorResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class AppTest {
    val client = HttpClient(CIO) {
        install(ContentNegotiation.Plugin) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(WebSockets)
        expectSuccess = false // semelhante ao validateStatus = () => true
    }

//    private lateinit var websocketSession: WebSocketSession

//    private var messages = mutableListOf<GetDepthOutput>()
    private lateinit var job: Job

//    @BeforeEach
//    fun setup() = runBlocking {
//        job = launch {
//            client.webSocket("ws://localhost:3000/ws") {
//                println("Oi estou no client")
//                for (frame in incoming) {
//                    println("chegou algo!")
//                    if (frame is Frame.Text) {
//                        println(frame.readText())
//                        val recv = Json.decodeFromString<GetDepthOutput>(frame.readText())
//                        messages.add(recv)
//                    }
//                }
//                println("fim")
//            }
//        }
//    }

    @Test
    fun `deve criar um conta válida`() = runBlocking {
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup: SignupOutput = responseSignup.body<SignupOutput>()
        Assertions.assertNotNull(outputSignup.accountId)

        val responseGet = client.get("http://localhost:3000/accounts/${outputSignup.accountId}")
        val outputGet = responseGet.body<AccountInput>()

        Assertions.assertEquals(inputSignup.name, outputGet.name)
        Assertions.assertEquals(inputSignup.email, outputGet.email)
        Assertions.assertEquals(inputSignup.document, outputGet.document)
    }

    @Test
    fun `não deve criar uma conta com nome inválido`() = runBlocking {
        val inputSignup = AccountInput(
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
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<SignupOutput>()
        val inputDeposit = DepositInput(
            accountId = outputSignup.accountId,
            assetId = "BTC/USD",
            quantity = 10
        )
        client.post("http://localhost:3000/deposit") {
            contentType(ContentType.Application.Json)
            setBody(inputDeposit)
        }
        val responseGetAccount = client.get("http://localhost:3000/accounts/${outputSignup.accountId}")
        val outputGetAccount = responseGetAccount.body<GetAccountOutput>()
        Assertions.assertEquals(1, outputGetAccount.assets.size)
        Assertions.assertEquals("BTC/USD", outputGetAccount.assets[0].assetId)
        Assertions.assertEquals(10, outputGetAccount.assets[0].quantity)
    }

    @Test
    fun `deve fazer um saque`() = runBlocking {
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<SignupOutput>()
        val inputDeposit = DepositInput(
            accountId = outputSignup.accountId,
            assetId = "BTC/USD",
            quantity = 10
        )
        client.post("http://localhost:3000/deposit") {
            contentType(ContentType.Application.Json)
            setBody(inputDeposit)
        }
        val inputWithdraw = WithDrawInput(
            accountId = outputSignup.accountId,
            assetId = "BTC/USD",
            quantity = 5
        )
        client.post("http://localhost:3000/withdraw") {
            contentType(ContentType.Application.Json)
            setBody(inputWithdraw)
        }
        val responseGetAccount = client.get("http://localhost:3000/accounts/${outputSignup.accountId}")
        val outputGetAccount = responseGetAccount.body<GetAccountOutput>()
        Assertions.assertEquals(1, outputGetAccount.assets.size)
        Assertions.assertEquals("BTC/USD", outputGetAccount.assets[0].assetId)
        Assertions.assertEquals(5, outputGetAccount.assets[0].quantity)
    }

    @Test
    fun `não deve fazer um saque quando sem fundos`() = runBlocking {
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<SignupOutput>()
        val inputDeposit = DepositInput(
            accountId = outputSignup.accountId,
            assetId = "BTC/USD",
            quantity = 5
        )
        client.post("http://localhost:3000/deposit") {
            contentType(ContentType.Application.Json)
            setBody(inputDeposit)
        }
        val inputWithdraw = WithDrawInput(
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
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<SignupOutput>()
        val inputPlaceOrder = OrderInput(
            marketId = "BTC/USD",
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94000
        )
        val responsePlaceOrder: HttpResponse = client.post("http://localhost:3000/place_order") {
            contentType(ContentType.Application.Json)
            setBody(inputPlaceOrder)
        }
        val outputPlaceOrder: OrderOutput = responsePlaceOrder.body<OrderOutput>()
        Assertions.assertNotNull(outputPlaceOrder.orderId)
        val responseGetOrder = client.get("http://localhost:3000/orders/${outputPlaceOrder.orderId}")
        val outputGetOrder = responseGetOrder.body<GetOrderOutput>()
        Assertions.assertEquals("BTC/USD", outputGetOrder.marketId)
        Assertions.assertEquals("sell", outputGetOrder.side)
        Assertions.assertEquals(1, outputGetOrder.quantity)
        Assertions.assertEquals(94000, outputGetOrder.price)
        Assertions.assertNotNull(outputGetOrder.timestamp)
    }

    @Test
    fun `deve criar ordens de compra e venda e executá-las`() = runBlocking {
        val messages = mutableListOf<GetDepthOutput>()
        job = launch {
            client.webSocket("ws://localhost:3000/ws") {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val recv = Json.decodeFromString<GetDepthOutput>(frame.readText())
                        messages.add(recv)
                    }
                }
            }
        }
        val marketId = "BTC/USD${Random.nextDouble()}"
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<SignupOutput>()

        val inputPlaceOrder1 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94000
        )
        val responsePlaceOrder1: HttpResponse = client.post("http://localhost:3000/place_order") {
            contentType(ContentType.Application.Json)
            setBody(inputPlaceOrder1)
        }
        val outputPlaceOrder1: OrderOutput = responsePlaceOrder1.body<OrderOutput>()

        val inputPlaceOrder2 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "buy",
            quantity = 1,
            price = 94500
        )
        val responsePlaceOrder2: HttpResponse = client.post("http://localhost:3000/place_order") {
            contentType(ContentType.Application.Json)
            setBody(inputPlaceOrder2)
        }
        val outputPlaceOrder2: OrderOutput = responsePlaceOrder2.body<OrderOutput>()

        val responseGetOrder1 = client.get("http://localhost:3000/orders/${outputPlaceOrder1.orderId}")
        val outputGetOrder1 = responseGetOrder1.body<GetOrderOutput>()
        Assertions.assertEquals("closed", outputGetOrder1.status)
        Assertions.assertEquals(1, outputGetOrder1.fillQuantity)
        Assertions.assertEquals(94000, outputGetOrder1.fillPrice)

        val responseGetOrder2 = client.get("http://localhost:3000/orders/${outputPlaceOrder2.orderId}")
        val outputGetOrder2 = responseGetOrder2.body<GetOrderOutput>()
        Assertions.assertEquals("closed", outputGetOrder2.status)
        Assertions.assertEquals(1, outputGetOrder2.fillQuantity)
        Assertions.assertEquals(94000, outputGetOrder2.fillPrice)

        val responseGetDepth: HttpResponse = client.get("http://localhost:3000/depth") {
            url { parameters.append("marketId", marketId) }
        }
        val outputGetDepth = responseGetDepth.body<GetDepthOutput>()
        Assertions.assertEquals(0, outputGetDepth.sells.size)
        Assertions.assertEquals(0, outputGetDepth.buys.size)

        Assertions.assertEquals(0, messages[0].buys.size)
        Assertions.assertEquals(1, messages[0].sells.size)
        Assertions.assertEquals(0, messages[1].buys.size)
        Assertions.assertEquals(0, messages[1].sells.size)

        val responseGetTrades: HttpResponse = client.get("http://localhost:3000/markets/trades") {
            url { parameters.append("marketId", marketId) }
        }
        val outputGetTrades = responseGetTrades.body<List<GetTradesOutput>>()
        Assertions.assertEquals(1, outputGetTrades.size)

        job.cancelAndJoin()
    }
}