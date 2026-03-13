package dev.wo.application.services

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.HttpException
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.domain.services.TransactionProcessor
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.adapters.FileService
import dev.wo.infrastructure.factories.TransactionProcessorFactory
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.io.File
import kotlin.test.*

class ProcessFileServiceTest {

    private lateinit var processFileService: ProcessFileService
    private val mockProcessor: TransactionProcessor = mockk(relaxed = true)

    @BeforeTest
    fun setUp() {
        mockkStatic(TransactionProcessorFactory::class)
        mockkStatic(FileService::class)
        processFileService = ProcessFileService()
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `process should throw HttpException for invalid file type`() {
        val file = File.createTempFile("test", ".txt")
        val preferences = ProcessorConfiguration()

        val exception = assertFailsWith<HttpException> {
            processFileService.process(FinancialInstitution.NUBANK, "txt", file, preferences)
        }

        assertEquals(HttpStatusCode.UnsupportedMediaType, exception.status)
        assertEquals("Invalid file type", exception.message)
    }

    @Test
    fun `process should throw HttpException when no transactions are found`() {
        val file = File.createTempFile("test", ".csv")
        val preferences = ProcessorConfiguration()
        val processingResult = ProcessingResult.success<List<FinancialTransaction>>(emptyList())

        every { TransactionProcessorFactory.getProcessor(FinancialInstitution.C6_BANK, "csv") } returns mockProcessor
        every { mockProcessor.processFile() } returns processingResult

        val exception = assertFailsWith<HttpException> {
            processFileService.process(FinancialInstitution.C6_BANK, "csv", file, preferences)
        }
        assertEquals(HttpStatusCode.NoContent, exception.status)
        assertEquals("No transactions found", exception.message)
    }
}