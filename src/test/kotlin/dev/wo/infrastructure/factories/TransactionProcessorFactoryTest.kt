package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.infrastructure.adapters.processors.NubankTransactionProcessor
import org.junit.Assert.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionProcessorFactoryTest {

        @Test
        fun `should return NubankTransactionProcessor`() {
            val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.NUBANK)
            assertTrue(processor is NubankTransactionProcessor)
        }

        @Test
        fun `should throw IllegalArgumentException`() {
            val exception = assertThrows(IllegalArgumentException::class.java) {
                TransactionProcessorFactory.getProcessor(FinancialInstitution.UNKNOWN)
            }

            assertEquals("Invalid financial institution", exception.message)
        }
}