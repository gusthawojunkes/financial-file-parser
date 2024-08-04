package dev.wo.application.web.resource.response

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.transactions.FinancialTransaction

data class FinancialTransactionResponse(
    val value: Double,
    val description: String,
    val transactionTime: String,
    val institutionUUID: String? = null,
    val transactionType: TransactionType,
    val institution: FinancialInstitution,
    val cardType: CardType? = null
) {
    companion object {
        fun from(financialTransaction: FinancialTransaction): FinancialTransactionResponse {
            return FinancialTransactionResponse(
                value = financialTransaction.value,
                description = financialTransaction.description,
                transactionTime = financialTransaction.getFormattedTransactionTime(),
                institutionUUID = financialTransaction.institutionUUID,
                transactionType = financialTransaction.transactionType,
                institution = financialTransaction.institution,
                cardType = financialTransaction.cardType
            )
        }
    }
}
