package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.InvoiceType
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.helpers.FileDataHelper
import dev.wo.infrastructure.helpers.TransactionFingerprintHelper
import java.io.File

class C6CSVTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorConfiguration? = null
) : CommonCSVTransactionProcessor(file, preferences) {

    override fun <T> createFinancialTransactions(data: T): MutableList<FinancialTransaction> {
        val transactions = mutableListOf<FinancialTransaction>()
        val dateTimePattern = preferences?.dateTimePattern ?: "dd/MM/yyyy"
        data as Sequence<Map<String, String>>

        for (row: Map<String, String> in data) {
            val value = row["Valor (em R$)"]?.toDouble() ?: continue
            val usdValue = row["Valor (em US$)"]?.toDouble()
            val description = row["Descrição"] ?: ""
            val transactionTime = FileDataHelper.getDate(row["Data de Compra"], dateTimePattern) ?: continue
            val transactionType = TransactionType.DEBIT // for now idk how can i distinct when its credit or debit
            val institution = FinancialInstitution.C6_BANK
            val cardType = if (preferences?.invoiceType == InvoiceType.CREDIT_INVOICE) CardType.CREDIT else CardType.DEBIT
            val currency = if (usdValue != null && usdValue > 0.0) "USD" else "BRL"
            val finalValue = if (transactionType == TransactionType.DEBIT) -value else value
            val identifier = TransactionFingerprintHelper.generate(
                institution = institution,
                transactionType = transactionType,
                value = finalValue,
                transactionTime = transactionTime,
                description = description,
                currency = currency
            )

            val transaction = FinancialTransaction(
                finalValue,
                description,
                transactionTime,
                identifier,
                transactionType,
                institution,
                cardType,
                currency,
                institutionUUID = null
            )

            transactions.add(transaction)
        }

        return transactions
    }
}