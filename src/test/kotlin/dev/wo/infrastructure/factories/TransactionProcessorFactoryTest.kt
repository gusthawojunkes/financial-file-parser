package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.infrastructure.adapters.processors.BradescoCSVTransactionProcessor
import dev.wo.infrastructure.adapters.processors.C6OFXTransactionProcessor
import dev.wo.infrastructure.adapters.processors.CommonCSVTransactionProcessor
import dev.wo.infrastructure.adapters.processors.CommonOFXTransactionProcessor
import dev.wo.infrastructure.adapters.processors.WiseCSVTransactionProcessor
import org.junit.Assert.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionProcessorFactoryTest {

    @Test
    fun `should return CommonOFXTransactionProcessor when institution is Nubank and file type is OFX`() {
        val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.NUBANK, "OFX")
        assertTrue(processor is CommonOFXTransactionProcessor)
    }

    @Test
    fun `should throw FileProcessingException when institution is Nubank and file type is unkown`() {

        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.NUBANK, "xyz")
        }

        assertEquals("Invalid file type for Nubank", exception.message)
    }

    @Test
    fun `should return WiseCSVTransactionProcessor when institution is Wise and file type is CSV`() {
        val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.WISE, "CSV")
        assertTrue(processor is WiseCSVTransactionProcessor)
    }

    @Test
    fun `should throw FileProcessingException when institution is Wise and file type is unkown`() {
        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.WISE, "xyz")
        }

        assertEquals("Invalid file type for Wise", exception.message)
    }

    @Test
    fun `should throw FileProcessingException when institution is Unkown`() {
        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.UNKNOWN, "unknown")
        }

        assertEquals("Invalid financial institution", exception.message)
    }

    @Test
    fun `should return CommonCSVTransactionProcessor when institution is Any`() {
        val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.ANY, "CSV")
        assertTrue(processor is CommonCSVTransactionProcessor)
    }

    @Test
    fun `should throw FileProcessingException when institution is Any and file type is unkown`() {
        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.ANY, "xyz")
        }

        assertEquals("Invalid file type for Any", exception.message)
    }

    @Test
    fun `should return C6OFXTransactionProcessor when institution is C6 and file type is OFX`() {
        val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.C6_BANK, "OFX")
        assertTrue(processor is C6OFXTransactionProcessor)
    }

    @Test
    fun `should throw FileProcessingException when institution is C6 and file type is unkown`() {
        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.C6_BANK, "???")
        }

        assertEquals("Invalid file type for C6 Bank", exception.message)
    }

    @Test
    fun `should return BradescoCSVTransactionProcessor when institution is C6 and file type is CSV`() {
        val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.BRADESCO, "CSV")
        assertTrue(processor is BradescoCSVTransactionProcessor)
    }

    @Test
    fun `should throw FileProcessingException when institution is Bradesco and file type is unkown`() {
        val exception = assertThrows(FileProcessingException::class.java) {
            TransactionProcessorFactory.getProcessor(FinancialInstitution.BRADESCO, "???")
        }

        assertEquals("Invalid file type for Bradesco", exception.message)
    }

}