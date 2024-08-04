package dev.wo.domain.transactions

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class FinancialTransaction(
    val value: Double,
    val description: String,
    val transactionTime: LocalDateTime,
    val institutionUUID: String? = null,
    val transactionType: TransactionType,
    val institution: FinancialInstitution,
    val cardType: CardType? = null
) {
    fun getFormattedTransactionTime(): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return transactionTime.format(formatter)
    }
}
