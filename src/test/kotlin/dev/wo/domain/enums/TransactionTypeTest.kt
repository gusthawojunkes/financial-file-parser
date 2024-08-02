package dev.wo.domain.enums

import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionTypeTest() {

        @Test
        fun `should return TransactionType from string`() {
            val transactionType = TransactionType.fromString("Crédito")
            assertEquals(TransactionType.CREDIT, transactionType)
        }

        @Test
        fun `should return TransactionType DEBIT from string`() {
            val transactionType = TransactionType.fromString("Débito")
            assertEquals(TransactionType.DEBIT, transactionType)
        }

        @Test
        fun `should return TransactionType PIX from string`() {
            val transactionType = TransactionType.fromString("Pix")
            assertEquals(TransactionType.PIX, transactionType)
        }

        @Test
        fun `should return TransactionType UNKNOWN from string`() {
            val transactionType = TransactionType.fromString("Unknown")
            assertEquals(TransactionType.UNKNOWN, transactionType)
        }
}