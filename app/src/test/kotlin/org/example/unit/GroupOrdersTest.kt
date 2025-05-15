package org.example.unit

import org.example.domain.Order
import org.example.domain.groupOrders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GroupOrdersTest {

    @Test
    fun `deve agrupar as ordens 1`() {
        val orders = listOf(
            Order.create("", "", "sell", 1, 94000.0),
            Order.create("", "", "sell", 1, 94000.0),
            Order.create("", "", "sell", 1, 94000.0)
        )
        val index = groupOrders(orders, 3)
        Assertions.assertEquals(3, index["sell"]!![94000.0])
    }

    @Test
    fun `deve agrupar as ordens 2`() {
        val orders = listOf(
            Order.create("", "", "sell", 1, 94000.0),
            Order.create("", "", "sell", 1, 94500.0),
            Order.create("", "", "sell", 1, 94600.0)
        )
        val index = groupOrders(orders, 3)
        Assertions.assertEquals(3, index["sell"]!![94000.0])
    }

    @Test
    fun `deve agrupar as ordens 3`() {
        val orders = listOf(
            Order.create("", "", "sell", 1, 94000.0),
            Order.create("", "", "sell", 1, 94500.0),
            Order.create("", "", "sell", 1, 94600.0)
        )
        val index = groupOrders(orders, 0)
        Assertions.assertEquals(1, index["sell"]!![94000.0])
        Assertions.assertEquals(1, index["sell"]!![94500.0])
        Assertions.assertEquals(1, index["sell"]!![94600.0])
    }
}