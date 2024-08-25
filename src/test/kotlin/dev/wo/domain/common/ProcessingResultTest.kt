package dev.wo.domain.common

import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessingResultTest {
    @Test
    fun `should create a success result`() {
        val result = ProcessingResult.success("data")
        assertEquals("Success", result.message)
        assertEquals(HttpStatusCode.OK, result.status)
        assertEquals("data", result.data)
    }

    @Test
    fun `should create an error result`() {
        val result = ProcessingResult.error<String>("error", HttpStatusCode.BadRequest)
        assertEquals("error", result.message)
        assertEquals(HttpStatusCode.BadRequest, result.status)
        assertEquals(null, result.data)
    }

}