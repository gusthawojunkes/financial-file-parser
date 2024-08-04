package dev.wo.domain.transactions

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import java.time.LocalDateTime

data class FinancialTransaction(
    val value: Double,
    val description: String,
    val transactionTime: LocalDateTime,
    val institutionUUID: String? = null,
    val transactionType: TransactionType,
    val institution: FinancialInstitution,
    val cardType: CardType? = null
)
