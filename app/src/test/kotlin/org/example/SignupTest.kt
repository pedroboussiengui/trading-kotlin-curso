package org.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

//@Disabled
class SignupTest {

    @Serializable
    data class Account(
        val accountId: String? = null,
        val name: String,
        val email: String,
        val document: String,
        val password: String? = null
    )

    @Serializable
    data class ErrorResponse(
        val error: String
    )

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
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
        assertNotNull(outputSignup.accountId)

        val responseGet = client.get("http://localhost:3000/accounts/${outputSignup.accountId}")
        val outputGet = responseGet.body<Account>()

        assertEquals(inputSignup.name, outputGet.name)
        assertEquals(inputSignup.email, outputGet.email)
        assertEquals(inputSignup.document, outputGet.document)
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
        assertEquals(HttpStatusCode.UnprocessableEntity, responseSignup.status)
        assertEquals("Invalid name", outputSignup.error)
    }

    @Test
    fun `não deve criar uma conta com email inválido`() = runBlocking {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe",
            document = "97456321558",
            password = "asdQWE123"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<ErrorResponse>()
        assertEquals(HttpStatusCode.UnprocessableEntity, responseSignup.status)
        assertEquals("Invalid email", outputSignup.error)
    }

    @Test
    fun `não deve criar uma conta com senha inválido`() = runBlocking {
        val inputSignup = Account(
            name = "John Doe",
            email = "john.doe@gmail.com",
            document = "97456321558",
            password = "asdQWE"
        )
        val responseSignup: HttpResponse = client.post("http://localhost:3000/signup") {
            contentType(ContentType.Application.Json)
            setBody(inputSignup)
        }
        val outputSignup = responseSignup.body<ErrorResponse>()
        assertEquals(HttpStatusCode.UnprocessableEntity, responseSignup.status)
        assertEquals("Invalid password", outputSignup.error)
    }
}