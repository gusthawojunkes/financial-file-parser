package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.exceptions.FileProcessingException
import org.junit.Assert.assertThrows
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NubankTransactionProcessorTest {

    @Test
    fun `when processFile is called without a file type then it should throw an FileProcessingException`() {
        val processor = NubankTransactionProcessor(fileType = null)

        val exception = assertThrows(FileProcessingException::class.java) {
            processor.processFile()
        }

        assertEquals("File type must be set", exception.message)
    }

    @Test
    fun `when processFile is called without a file then it should throw an FileProcessingException`() {
        val processor = NubankTransactionProcessor(file = null, fileType = "ofx")

        val exception = assertThrows(FileProcessingException::class.java) {
            processor.processFile()
        }

        assertEquals("File must be set", exception.message)
    }

    @Test
    fun `when processFile is called with an invalid file type then it should throw an FileProcessingException`() {
        val processor = NubankTransactionProcessor(
            file = File("src/test/resources/files/nubank.ofx"),
            fileType = "invalid"
        )

        val exception = assertThrows(FileProcessingException::class.java) {
            processor.processFile()
        }

        assertEquals("Invalid file type", exception.message)
    }

    @Test
    fun `when processFile is called with a valid file type and a non empty file then it should return a list of transactions`() {
        val processor = NubankTransactionProcessor(
            file = File("src/test/resources/files/nubank.ofx"),
            fileType = "ofx"
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isNotEmpty())
    }

}