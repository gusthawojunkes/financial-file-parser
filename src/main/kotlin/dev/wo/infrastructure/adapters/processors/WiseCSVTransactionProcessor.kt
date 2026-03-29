package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.helpers.FileDataHelper
import dev.wo.infrastructure.helpers.TransactionFingerprintHelper
import java.io.File

class WiseCSVTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorConfiguration? = null
) : CommonCSVTransactionProcessor(file, preferences) {

    override fun processFile(): ProcessingResult<List<FinancialTransaction>> {
        return super.processFile()
    }

    override fun <T> createFinancialTransactions(data: T): MutableList<FinancialTransaction> {
        val transactions = mutableListOf<FinancialTransaction>()
        val dateTimePattern = preferences?.dateTimePattern ?: "dd-MM-yyyy"
        data as Sequence<Map<String, String>>

        for (row: Map<String, String> in data) {
            val value = row["Amount"]?.toDouble() ?: continue
            val description = row["Description"] ?: ""
            val transactionTime = FileDataHelper.getDate(row["Date"], dateTimePattern) ?: continue
            val transactionType = if (value > 0) TransactionType.CREDIT else TransactionType.DEBIT
            val institution = FinancialInstitution.WISE
            val cardType = CardType.DEBIT // wise doesnt have credit cards
            val currency = row["Currency"] ?: continue
            val cardLastFourDigits = row["Card Last Four Digits"]?.takeIf { it.isNotBlank() }
            val identifier = TransactionFingerprintHelper.generate(
                institution = institution,
                transactionType = transactionType,
                value = value,
                transactionTime = transactionTime,
                description = description,
                currency = currency
            )

            val transaction = FinancialTransaction(
                value,
                description,
                transactionTime,
                identifier,
                transactionType,
                institution,
                cardType,
                currency,
                institutionUUID = null,
                cardLastFourDigits = cardLastFourDigits
            )

            transactions.add(transaction)
        }

        return transactions
    }
}