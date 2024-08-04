package dev.wo.application.web.resource.response

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType

data class FinancialTransactionResponse(
    val value: Double,
    val description: String,
    val transactionTime: String,
    val institutionUUID: String? = null,
    val transactionType: TransactionType,
    val institution: FinancialInstitution,
    val cardType: CardType? = null
)
