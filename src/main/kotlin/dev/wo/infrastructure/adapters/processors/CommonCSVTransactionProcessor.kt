package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.services.TransactionProcessor
import dev.wo.domain.transactions.FinancialTransaction
import java.io.File

class CommonCSVTransactionProcessor(
    override var file: File? = null
) : TransactionProcessor {
    override fun processFile(): MutableList<FinancialTransaction> {
        TODO("Not yet implemented")
    }

    override fun <T> createFinancialTransactions(data: T): MutableList<FinancialTransaction> {
        TODO("Not yet implemented")
    }
}