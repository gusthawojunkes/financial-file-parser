package dev.wo.plugins

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {

    @Test
    public fun `health check endpoint must ruturn 'Up and running!'`() = testApplication {
        val response = client.get("/health-check")
        assertEquals("Up and running!", response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
    }
    
}