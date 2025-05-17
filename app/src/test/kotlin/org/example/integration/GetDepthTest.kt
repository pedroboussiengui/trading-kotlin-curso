package org.example.integration

import kotlinx.coroutines.runBlocking
import kotliquery.sessionOf
import org.example.application.usecase.*
import org.example.infra.database.PostgresDataSource
import org.example.infra.repository.AccountRepositoryDatabase
import org.example.infra.repository.OrderRepositoryDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDepthTest {

    lateinit var signup: SignUp
    lateinit var placeOrder: PlaceOrder
    lateinit var getDepth: GetDepth
    lateinit var marketId: String

    @BeforeEach
    fun setup() {
        val dataSource = PostgresDataSource.dataSource
        val session = sessionOf(dataSource)
        val orderRepository = OrderRepositoryDatabase(session)
        orderRepository.deleteAll()
        val accountRepository = AccountRepositoryDatabase(session)
        placeOrder = PlaceOrder(orderRepository)
        signup = SignUp(accountRepository)
        getDepth = GetDepth(orderRepository)
        marketId = "BTC/USD"
    }

    @Test
    fun `deve retornar o depth após o a realização de ordens de compra e venda`() = runBlocking {
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val outputSignup: SignupOutput = signup.execute(inputSignup)

        val inputPlaceOrder1 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94000.0
        )
        placeOrder.execute(inputPlaceOrder1)

        val inputPlaceOrder2 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94500.0
        )
        placeOrder.execute(inputPlaceOrder2)

        val inputPlaceOrder3 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94600.0
        )
        placeOrder.execute(inputPlaceOrder3)

        val outputGetDepth = getDepth.execute(marketId, 0)

        Assertions.assertEquals(3, outputGetDepth.sells.size)

        Assertions.assertEquals(1, outputGetDepth.sells[0].quantity)
        Assertions.assertEquals(94000.0, outputGetDepth.sells[0].price)

        Assertions.assertEquals(1, outputGetDepth.sells[1].quantity)
        Assertions.assertEquals(94500.0, outputGetDepth.sells[1].price)

        Assertions.assertEquals(1, outputGetDepth.sells[2].quantity)
        Assertions.assertEquals(94600.0, outputGetDepth.sells[2].price)

        Assertions.assertEquals(0, outputGetDepth.buys.size)
    }

    @Test
    fun `deve retornar o depth após o a realização de ordens de compra e venda sem precision mas com valores iguais`() = runBlocking {
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val outputSignup: SignupOutput = signup.execute(inputSignup)

        val inputPlaceOrder1 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94000.0
        )
        placeOrder.execute(inputPlaceOrder1)

        val inputPlaceOrder2 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94000.0
        )
        placeOrder.execute(inputPlaceOrder2)

        val inputPlaceOrder3 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94000.0
        )
        placeOrder.execute(inputPlaceOrder3)

        val outputGetDepth = getDepth.execute(marketId, 0)

        Assertions.assertEquals(1, outputGetDepth.sells.size)

        Assertions.assertEquals(3, outputGetDepth.sells[0].quantity)
        Assertions.assertEquals(94000.0, outputGetDepth.sells[0].price)

        Assertions.assertEquals(0, outputGetDepth.buys.size)
    }

    @Test
    fun `deve retornar o depth após o a realização de ordens de compra e venda com precision de 3 casas`() = runBlocking {
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val outputSignup: SignupOutput = signup.execute(inputSignup)

        val inputPlaceOrder1 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94000.0
        )
        placeOrder.execute(inputPlaceOrder1)

        val inputPlaceOrder2 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94500.0
        )
        placeOrder.execute(inputPlaceOrder2)

        val inputPlaceOrder3 = OrderInput(
            marketId = marketId,
            accountId = outputSignup.accountId,
            side = "sell",
            quantity = 1,
            price = 94500.0
        )
        placeOrder.execute(inputPlaceOrder3)

        val outputGetDepth = getDepth.execute(marketId, 3)

        Assertions.assertEquals(1, outputGetDepth.sells.size)

        Assertions.assertEquals(3, outputGetDepth.sells[0].quantity)
        Assertions.assertEquals(94000.0, outputGetDepth.sells[0].price)

        Assertions.assertEquals(0, outputGetDepth.buys.size)
    }
}