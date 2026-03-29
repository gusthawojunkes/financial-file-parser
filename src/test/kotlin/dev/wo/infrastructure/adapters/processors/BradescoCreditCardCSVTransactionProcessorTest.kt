package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BradescoCreditCardCSVTransactionProcessorTest {

    private val testFile = File("src/test/resources/files/csv/bradesco_credit_invoice.csv")

    @Test
    fun `when processFile is called with a valid credit card invoice then it should return a non-empty list`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `when processFile is called then all transactions should belong to BRADESCO`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.all { it.institution == FinancialInstitution.BRADESCO })
    }

    @Test
    fun `when processFile is called then all transactions should have CREDIT card type`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.all { it.cardType == CardType.CREDIT })
    }

    @Test
    fun `when processFile is called then expense transactions should be DEBIT type with negative value`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        val expenses = result.data.filter { it.transactionType == TransactionType.DEBIT }
        assertTrue(expenses.isNotEmpty())
        assertTrue(expenses.all { it.value < 0.0 })
    }

    @Test
    fun `when processFile is called then payment transactions should be CREDIT type with positive value`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        val payments = result.data.filter { it.transactionType == TransactionType.CREDIT }
        assertTrue(payments.isNotEmpty())
        assertTrue(payments.all { it.value > 0.0 })
    }

    @Test
    fun `when processFile is called then transactions from first card should have correct last four digits`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        val firstCardTransactions = result.data.filter { it.cardLastFourDigits == "1234" }
        assertTrue(firstCardTransactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called then transactions from second card should have correct last four digits`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        val secondCardTransactions = result.data.filter { it.cardLastFourDigits == "5678" }
        assertTrue(secondCardTransactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called then total line and summary should not be parsed as transactions`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.none { it.description.startsWith("Total da fatura") })
        assertTrue(result.data.none { it.description.startsWith("Resumo das Despesas") })
    }

    @Test
    fun `when processFile is called then transactions with USD value should have USD currency`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        val usdTransactions = result.data.filter { it.currency == "USD" }
        assertTrue(usdTransactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called then transactions without USD value should have BRL currency`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        val brlTransactions = result.data.filter { it.currency == "BRL" }
        assertTrue(brlTransactions.isNotEmpty())
    }

    @Test
    fun `when processFile is called then total transaction count should match expected`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        // 5 from first card + 3 from second card = 8 total
        assertNotNull(result.data)
        assertEquals(8, result.data.size)
    }

    @Test
    fun `when processFile is called with null file then it should return empty list`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = null)

        val result = processor.processFile()

        assertNotNull(result.data)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `when processFile is called then each transaction should have a unique identifier`() {
        val processor = BradescoCreditCardCSVTransactionProcessor(file = testFile)

        val result = processor.processFile()

        assertNotNull(result.data)
        val identifiers = result.data.map { it.identifier }.toSet()
        assertEquals(result.data.size, identifiers.size)
    }
}

