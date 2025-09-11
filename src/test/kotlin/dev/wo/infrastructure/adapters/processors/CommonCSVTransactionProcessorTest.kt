package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.services.ProcessorConfiguration
import java.io.File
import kotlin.test.*

class CommonCSVTransactionProcessorTest {
    @Test
    fun `when processFile is called with a non empty comma separated file then it should return a list of transactions`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_comma.csv")
        )

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `when processFile is called with comma separated file then the file should be processed normally`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_comma.csv"),
            preferences = ProcessorConfiguration(csvSeparator = ',')
        )

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `when processFile is called with semicolon separated file then the file should be processed normally`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_semicolon.csv"),
            preferences = ProcessorConfiguration(csvSeparator = ';')
        )

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `when processFile is called with a semicolon separated file but without defining the separator configuration then it should return an empty list`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_semicolon.csv")
        )

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `when processFile is called with a comma separated file but defining the separator configuration as semicolon then it should return an empty list`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/c6_comma.csv"),
            preferences = ProcessorConfiguration(csvSeparator = ';')
        )

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `should process a big csv file`() {
        val processor = CommonCSVTransactionProcessor(
            file = File("src/test/resources/files/csv/large-transactions.csv"),
            preferences = ProcessorConfiguration(csvSeparator = ';', dateTimePattern = "yyyyMMddHHmmss")
        )

        val result = processor.processFile()

        assertNotNull(result.data)
        assertEquals(10000, result.data.size)
    }

}