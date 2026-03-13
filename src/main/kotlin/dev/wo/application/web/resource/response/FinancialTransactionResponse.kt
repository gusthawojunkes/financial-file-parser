package dev.wo.application.web.resource.response

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.transactions.FinancialTransaction

data class FinancialTransactionResponse(
    val value: Double,
    val description: String,
    val transactionTime: String,
    val identifier: String,
    val transactionType: TransactionType,
    val institution: FinancialInstitution,
    val cardType: CardType? = null,
    val currency: String = "BRL",
    val institutionUUID: String?
) {
    companion object {
        fun from(financialTransaction: FinancialTransaction): FinancialTransactionResponse {
            return FinancialTransactionResponse(
                value = financialTransaction.value,
                description = financialTransaction.description,
                transactionTime = financialTransaction.getFormattedTransactionTime(),
                identifier = financialTransaction.identifier,
                transactionType = financialTransaction.transactionType,
                institution = financialTransaction.institution,
                cardType = financialTransaction.cardType,
                currency = financialTransaction.currency,
                institutionUUID = financialTransaction.institutionUUID
            )
        }
    }
}
