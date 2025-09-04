package dev.wo.plugins

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class RoutingTest {

    @Test
    fun `health check endpoint must ruturn 'Up and running!'`() = testApplication {
        val response = client.get("/health-check")
        assertEquals("Up and running!", response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `demo endpoint must redirect to index html`() = testApplication {
        val response = client.get("/demo")
        assertEquals(HttpStatusCode.OK, response.status)
    }
    
}