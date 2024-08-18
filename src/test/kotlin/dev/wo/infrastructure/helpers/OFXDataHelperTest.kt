package dev.wo.infrastructure.helpers

import dev.wo.domain.enums.TransactionType
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileDataHelperTest {

    @Test
    fun `should return LocalDateTime when date is valid`() {
        val date = FileDataHelper.getDate("20210101000000")
        assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0, 0), date)
    }

    @Test
    fun `should return null when date is null`() {
        val date = FileDataHelper.getDate(null)
        assertNull(date)
    }

    @Test
    fun `should return null when date is invalid`() {
        val date = FileDataHelper.getDate("20210101")
        assertNull(date)
    }

    @Test
    fun `should return TransactionType CREDIT when type is CREDIT`() {
        val transactionType = FileDataHelper.getTransactionType("CREDIT")
        assertEquals(TransactionType.CREDIT, transactionType)
    }

    @Test
    fun `should return TransactionType DEBIT when type is DEBIT`() {
        val transactionType = FileDataHelper.getTransactionType("DEBIT")
        assertEquals(TransactionType.DEBIT, transactionType)
    }

    @Test
    fun `should return TransactionType PIX when type is PIX`() {
        val transactionType = FileDataHelper.getTransactionType("PIX")
        assertEquals(TransactionType.PIX, transactionType)
    }

    @Test
    fun `should return TransactionType UNKNOWN when type is unknown`() {
        val transactionType = FileDataHelper.getTransactionType("unknown")
        assertEquals(TransactionType.UNKNOWN, transactionType)
    }

    @Test
    fun `should return TransactionType UNKNOWN when type is null`() {
        val transactionType = FileDataHelper.getTransactionType(null)
        assertEquals(TransactionType.UNKNOWN, transactionType)
    }

    @Test

    fun `should return TransactionType UNKNOWN when type is not found`() {
        val transactionType = FileDataHelper.getTransactionType("abc123")
        assertEquals(TransactionType.UNKNOWN, transactionType)
    }

}