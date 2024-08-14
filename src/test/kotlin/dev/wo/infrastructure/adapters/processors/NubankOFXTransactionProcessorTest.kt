package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.exceptions.FileProcessingException
import org.junit.Assert.assertThrows
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NubankOFXTransactionProcessorTest {

    @Test
    fun `when processFile is called without a file then it should throw an FileProcessingException`() {
        val processor = NubankOFXTransactionProcessor(file = null)

        val exception = assertThrows(FileProcessingException::class.java) {
            processor.processFile()
        }

        assertEquals("File must be set", exception.message)
    }

    @Test
    fun `when processFile is called with a non empty file then it should return a list of transactions`() {
        val processor = NubankOFXTransactionProcessor(
            file = File("src/test/resources/files/nubank.ofx")
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isNotEmpty())
    }

}