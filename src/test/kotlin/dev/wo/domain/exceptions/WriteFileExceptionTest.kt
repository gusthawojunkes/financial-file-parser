package dev.wo.domain.exceptions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WriteFileExceptionTest {

    @Test
    fun initializesWithProvidedMessage() {
        val message = "File could not be written"
        val exception = WriteFileException(message)

        assertEquals(message, exception.message)
    }

    @Test
    fun initializesWithEmptyMessage() {
        val message = ""
        val exception = WriteFileException(message)

        assertEquals(message, exception.message)
    }

}