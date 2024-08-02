package dev.wo.domain.models.ofx

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import java.time.LocalDateTime

data class FinancialTransaction(
    val value: Double,
    val description: String,
    val transactionTime: LocalDateTime,
    val institutionUuid: String? = null,
    val transactionType: TransactionType,
    val institution: FinancialInstitution
)
