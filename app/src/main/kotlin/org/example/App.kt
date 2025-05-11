package org.example

fun main() {

    val accountRepository = AccountRepositoryDatabase()
    val orderRepository = OrderRepositoryDatabase()

    val signup = SignUp(accountRepository)
    val deposit = Deposit(accountRepository)
    val withdraw = WithDraw(accountRepository)
    val getAccount = GetAccount(accountRepository)
    val placeOrder = PlaceOrder(orderRepository)
    val getOrder = GetOrder(orderRepository)

    KtorAdapter(
        signup,
        deposit,
        withdraw,
        getAccount,
        placeOrder,
        getOrder
    ).start()
}
