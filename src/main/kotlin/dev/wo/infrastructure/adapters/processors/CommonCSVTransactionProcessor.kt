package dev.wo.infrastructure.adapters.processors

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.domain.services.TransactionProcessor
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.helpers.FileDataHelper
import java.io.File

class CommonCSVTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorConfiguration? = null
) : TransactionProcessor {

    override fun processFile(): ProcessingResult<List<FinancialTransaction>> {
        val transactions = mutableListOf<FinancialTransaction>()
        val reader = csvReader {
            delimiter = preferences?.csvSeparator ?: ','
        }

        file?.let {
            return reader.open(it) {
                val parsedCSV = readAllWithHeaderAsSequence()
                return@open ProcessingResult.success(createFinancialTransactions(parsedCSV))
            }
        }
        return ProcessingResult.success(transactions)
    }

    override fun <T> createFinancialTransactions(data: T): MutableList<FinancialTransaction> {
        val transactions = mutableListOf<FinancialTransaction>()
        val dateTimePattern = preferences?.dateTimePattern ?: "dd/MM/yyyy HH:mm:ss"
        data as Sequence<Map<String, String>>

        for (row: Map<String, String> in data) {
            val value = row["value"]?.toDouble() ?: continue
            val description = row["description"] ?: ""
            val transactionTime = FileDataHelper.getDate(row["transactionTime"], dateTimePattern) ?: continue
            val institutionUUID = row["institutionUUID"] ?: FileDataHelper.generateUUID()
            val transactionType = FileDataHelper.getTransactionType(row["transactionType"])
            val institution = FinancialInstitution.fromString(row["institution"] ?: "")
            val cardType = CardType.fromString(row["cardType"] ?: "")

            val transaction = FinancialTransaction(
                value,
                description,
                transactionTime,
                institutionUUID,
                transactionType,
                institution,
                cardType
            )

            transactions.add(transaction)
        }

        return transactions
    }
}