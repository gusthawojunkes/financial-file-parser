package dev.wo.infrastructure.helpers

import dev.wo.domain.enums.TransactionType
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileDataHelperTest {

    @Test
    fun `should return LocalDateTime when date is valid`() {
        val date = FileDataHelper.getDateTime("20210101000000")
        assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0, 0), date)
    }

    @Test
    fun `getDateTime should return null when date is null`() {
        val date = FileDataHelper.getDateTime(null)
        assertNull(date)
    }

    @Test
    fun `getDateTime should return null when date is invalid`() {
        val date = FileDataHelper.getDateTime("20210101")
        assertNull(date)
    }

    @Test
    fun `should return LocalDateTime at start of day when date is valid`() {
        val date = FileDataHelper.getDate("20210101000000")
        assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0, 0), date)
    }

    @Test
    fun `getDate should return null when date is null`() {
        val date = FileDataHelper.getDate(null)
        assertNull(date)
    }

    @Test
    fun `getDate should return null when date is invalid`() {
        val date = FileDataHelper.getDate("20210101")
        assertNull(date)
    }

    @Test
    fun `should return null when date contains invalid characters`() {
        val date = FileDataHelper.getDate("2021-01-01")
        assertNull(date)
    }

    @Test
    fun `should ignore timezone information in date`() {
        val date = FileDataHelper.getDate("20210101000000[UTC]")
        assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0, 0), date)
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

    @Test
    fun `should return UUID when UUID is valid`() {
        val uuid = FileDataHelper.generateUUID()
        assertEquals(36, uuid.length)
    }

    @Test
    fun `should return UUID with a valid pattern`() {
        val uuid = FileDataHelper.generateUUID()
        assertEquals("8-4-4-4-12", uuid.split("-").map { it.length }.joinToString("-"))
    }

}