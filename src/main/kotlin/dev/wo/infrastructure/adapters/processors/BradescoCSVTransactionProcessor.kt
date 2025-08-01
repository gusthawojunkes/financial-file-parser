package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.domain.transactions.FinancialTransaction
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BradescoCSVTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorConfiguration? = null
) : CommonCSVTransactionProcessor(file, preferences) {

    override fun processFile(): ProcessingResult<List<FinancialTransaction>> {
        file?.let {
            val lines = it.useLines { seq -> seq.drop(2).toList() }
            if (lines.isEmpty()) {
                return ProcessingResult.success(emptyList())
            }
            return ProcessingResult.success(createFinancialTransactions(lines))
        }
        return ProcessingResult.success(emptyList())
    }

    override fun <T> createFinancialTransactions(data: T): MutableList<FinancialTransaction> {
        val transactions = mutableListOf<FinancialTransaction>()
        val dateTimePattern = preferences?.dateTimePattern ?: "dd/MM/yyyy"
        val delimiter = preferences?.csvSeparator ?: ';'
        val lines = data as ArrayList<String>

        for (row: String in lines) {
            if (";;;;;".equals(row, ignoreCase = true)) {
                break
            }
            val properties = row.split(delimiter)
            val transactionTime = properties[0]

            val formatter = DateTimeFormatter.ofPattern(dateTimePattern)
            val localDate = LocalDate.parse(transactionTime, formatter)
            val localDateTime = LocalDateTime.of(localDate, LocalTime.MIDNIGHT)
            val description = properties[1]
            if ("SALDO ANTERIOR" == description) {
                continue
            }
            val institutionUUID = properties[2]
            val creditValue = getDoubleValueFromString(properties[3])
            val debitValue = getDoubleValueFromString(properties[4])
            val isDebit = (creditValue == 0.0 && debitValue != 0.0)
            val transaction = FinancialTransaction(
                if (isDebit) -debitValue else creditValue,
                description,
                localDateTime,
                institutionUUID,
                if (isDebit) TransactionType.DEBIT else TransactionType.CREDIT,
                FinancialInstitution.BRADESCO,
                CardType.DEBIT
            )

            transactions.add(transaction)
        }

        return transactions
    }

    fun getDoubleValueFromString(value: String): Double {
        if ("" == value.trim()) return BigDecimal("0.00").toDouble()

        return BigDecimal(value.trim()
            .replace(".", "")
            .replace(',', '.'))
            .toDouble()
    }


}