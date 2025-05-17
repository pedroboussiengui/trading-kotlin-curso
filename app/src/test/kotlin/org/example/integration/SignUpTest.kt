package org.example.integration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotliquery.sessionOf
import org.example.application.usecase.AccountInput
import org.example.infra.repository.AccountRepositoryDatabase
import org.example.application.usecase.GetAccount
import org.example.application.usecase.SignUp
import org.example.application.usecase.SignupOutput
import org.example.infra.database.PostgresDataSource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SignUpTest {

    lateinit var signup: SignUp
    lateinit var getAccount: GetAccount

    @BeforeEach
    fun setup() {
        val dataSource = PostgresDataSource.dataSource
        val session = sessionOf(dataSource)
        val accountDAO = AccountRepositoryDatabase(session)
        signup = SignUp(accountDAO)
        getAccount = GetAccount(accountDAO)
    }

    /**
     * Apenas um caso de sucesso e um caso de fracasso
     * sao testados a nivel teste de integração
     */

    @Test
    fun `deve criar um conta válida`() {
        val inputSignup = AccountInput(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val outputSignup: SignupOutput = signup.execute(inputSignup)
        Assertions.assertNotNull(outputSignup)
        val outputGet = getAccount.execute(outputSignup.accountId)
        Assertions.assertEquals(inputSignup.name, outputGet.name)
        Assertions.assertEquals(inputSignup.email, outputGet.email)
        Assertions.assertEquals(inputSignup.document, outputGet.document)
    }

    @Test
    fun `não deve criar um conta com um nome inválido`() {
        val inputSignup = AccountInput(
            name = "John",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE123"
        )
        val exception = assertThrows<Exception> {
            signup.execute(inputSignup)
        }
        Assertions.assertEquals("Invalid name", exception.message)
    }

    /**
     * Testes não necessários, porque esses casos excepcionais devem ser testados
     * as nivel de unidade
     */
//    @Test
//    fun `não deve criar um conta com um email inválido`() {
//        val inputSignup = AccountInput(
//            name = "John Doe",
//            email = "john.doe",
//            document = "97456321558",
//            password = "asdQWE123"
//        )
//        val exception = assertThrows<Exception> {
//            signup.execute(inputSignup)
//        }
//        Assertions.assertEquals("Invalid email", exception.message)
//    }
//
//    @Test
//    fun `não deve criar um conta com um documeno inválido`() {
//        val inputSignup = AccountInput(
//            name = "John Doe",
//            email = "john.doe@gmail.com",
//            document = "974563215",
//            password = "asdQWE123"
//        )
//        val exception = assertThrows<Exception> {
//            signup.execute(inputSignup)
//        }
//        Assertions.assertEquals("Invalid document", exception.message)
//    }
//
//    @Test
//    fun `não deve criar um conta com uma senha inválida`() {
//        val inputSignup = AccountInput(
//            name = "John Doe",
//            email = "john.doe@gmail.com",
//            document = "974563215",
//            password = "asdQWE"
//        )
//        val exception = assertThrows<Exception> {
//            signup.execute(inputSignup)
//        }
//        Assertions.assertEquals("Invalid password", exception.message)
//    }

//    /**
//      * Sobrescrever um comportmento com algo fixo
//     */
//    @Test
//    fun `deve criar um conta válida com stub`() {
//        val accountStub = mockk<AccountRepositoryDatabase>()
//        // stub para saveAccount, não faz nada
//        every { accountStub.saveAccount(any()) } returns Unit
//        val inputSignup = AccountInput(
//            name = "John Doe",
//            email = "john.doe@gmail.com",
//            document = "97456321558",
//            password = "asdQWE123"
//        )
//        // Stub para getAccountById - retorna o inputSignup quando chamado com qualquer argument
//        every { accountStub.getAccountById(any()) } returns inputSignup
//        // Stub para getAccountAssets - retorna lista vazia
//        every { accountStub.getAccountAssets(any()) } returns emptyList()
//        val outputSignup: String = signup.execute(inputSignup)
//        Assertions.assertNotNull(outputSignup)
//        val outputGet = getAccount.execute(outputSignup)
//        Assertions.assertEquals(inputSignup.name, outputGet.name)
//        Assertions.assertEquals(inputSignup.email, outputGet.email)
//        Assertions.assertEquals(inputSignup.document, outputGet.document)
//    }
//
//    /**
//     * Quando eu quero se algo foi chamado e como foi chamado
//     * ele acessa o serviço real, nesse caso o banco
//     */
//    @Test
//    fun `deve criar um conta válida com spy`() {
//        val accountSpy = spyk<AccountDAODatabase>()
//        signup = SignUp(accountSpy)
//        getAccount = GetAccount(accountSpy)
//        val inputSignup = AccountInput(
//            name = "John Doe",
//            email = "john.doe@gmail.com",
//            document = "97456321558",
//            password = "asdQWE123"
//        )
//        val outputSignup = signup.execute(inputSignup)
//        Assertions.assertNotNull(outputSignup.accountId)
//        val outputGet = getAccount.execute(outputSignup.accountId!!)!!
//        Assertions.assertEquals(inputSignup.name, outputGet.name)
//        Assertions.assertEquals(inputSignup.email, outputGet.email)
//        Assertions.assertEquals(inputSignup.document, outputGet.document)
//
//        verify(exactly = 1) {
//            accountSpy.saveAccount(
//                match { savedAccount ->
//                    savedAccount.name == inputSignup.name &&
//                    savedAccount.email == inputSignup.email &&
//                    savedAccount.document == inputSignup.document &&
//                    savedAccount.accountId == outputSignup.accountId
//                }
//            )
//        }
//    }
//
//    /**
//     * mocka um objeto inteiro
//     */
//    @Test
//    fun `deve criar um conta válida com mock`() {
//        val accountMock = mockk<AccountDAODatabase>()
//        val inputSignup = AccountInput(
//            name = "John Doe",
//            email = "john.doe@gmail.com",
//            document = "97456321558",
//            password = "asdQWE123"
//        )
//        every { accountMock.saveAccount(any()) } returns Unit
//        every { accountMock.getAccountById(any()) } returns inputSignup
//        every { accountMock.getAccountAssets(any()) } returns emptyList()
//        signup = SignUp(accountMock)       // Usa o mock
//        getAccount = GetAccount(accountMock) // Usa o mock
//
//        val outputSignup = signup.execute(inputSignup)
//        Assertions.assertNotNull(outputSignup.accountId)
//        val outputGet = getAccount.execute(outputSignup.accountId!!)!!
//        Assertions.assertEquals(inputSignup.name, outputGet.name)
//        Assertions.assertEquals(inputSignup.email, outputGet.email)
//        Assertions.assertEquals(inputSignup.document, outputGet.document)
//
//        verify(exactly = 1) { accountMock.saveAccount(any()) }
//        verify(exactly = 1) { accountMock.getAccountById(any()) }
//        verify(exactly = 1) { accountMock.getAccountAssets(any()) }
//    }
}