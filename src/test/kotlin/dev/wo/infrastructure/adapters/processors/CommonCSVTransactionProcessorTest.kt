package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.services.ProcessorPreferences
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class CommonCSVTransactionProcessorTest {
    @Test
    fun `when processFile is called with a non empty comma separated file then it should return a list of transactions`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_comma.csv")
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called with comma separated file then the file should be processed normally`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_comma.csv"),
            preferences = ProcessorPreferences(csvSeparator = ',')
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called with semicolon separated file then the file should be processed normally`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_semicolon.csv"),
            preferences = ProcessorPreferences(csvSeparator = ';')
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called with a semicolon separated file but without defining the separator configuration then it should return an empty list`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_semicolon.csv")
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isEmpty())
    }

    @Test
    fun `when processFile is called with a comma separated file but defining the separator configuration as semicolon then it should return an empty list`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_comma.csv"),
            preferences = ProcessorPreferences(csvSeparator = ';')
        )

        val transactions = processor.processFile()

        assertTrue(transactions.isEmpty())
    }

}