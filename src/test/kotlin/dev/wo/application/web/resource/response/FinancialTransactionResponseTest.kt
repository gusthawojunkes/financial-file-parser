package dev.wo.application.web.resource.response

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.transactions.FinancialTransaction
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class FinancialTransactionResponseTest {

    @Test
    fun `should create a FinancialTransactionResponse from a FinancialTransaction`() {
        val financialTransaction = FinancialTransaction(
            value = 100.0,
            description = "Test",
            transactionTime = LocalDateTime.of(2021, 4, 3, 12, 23, 45),
            institutionUUID = "123",
            transactionType = TransactionType.DEBIT,
            institution = FinancialInstitution.NUBANK,
            cardType = CardType.CREDIT
        )

       val financialTransactionResponse = FinancialTransactionResponse.from(financialTransaction)

        assertEquals(100.0, financialTransactionResponse.value)
        assertEquals("Test", financialTransactionResponse.description)
        assertEquals("03/04/2021 12:23:45", financialTransactionResponse.transactionTime)
        assertEquals("123", financialTransactionResponse.institutionUUID)
        assertEquals(TransactionType.DEBIT, financialTransactionResponse.transactionType)
        assertEquals(FinancialInstitution.NUBANK, financialTransactionResponse.institution)
        assertEquals(CardType.CREDIT, financialTransactionResponse.cardType)
    }

}