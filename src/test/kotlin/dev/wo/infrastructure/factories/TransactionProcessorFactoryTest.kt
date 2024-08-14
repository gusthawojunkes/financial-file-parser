package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.infrastructure.adapters.processors.NubankOFXTransactionProcessor
import org.junit.Assert.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionProcessorFactoryTest {

    @Test
    fun `should return NubankOFXTransactionProcessor`() {
        val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.NUBANK, "OFX")
        assertTrue(processor is NubankOFXTransactionProcessor)
    }

    @Test
    fun `should throw FileProcessingException for Nubank when file type is unkown`() {

        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.NUBANK, "xyz")
        }

        assertEquals("Invalid file type for Nubank", exception.message)
    }

    @Test
    fun `should throw FileProcessingException`() {
        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.UNKNOWN, "unknown")
        }

        assertEquals("Invalid financial institution", exception.message)
    }
    
}