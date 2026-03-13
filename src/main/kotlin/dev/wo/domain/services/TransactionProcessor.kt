package dev.wo.domain.services

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.transactions.FinancialTransaction
import java.io.File

interface TransactionProcessor {
    var file: File?
    var preferences: ProcessorConfiguration?

    @Throws(FileProcessingException::class)
    fun processFile(): ProcessingResult<List<FinancialTransaction>>

    fun <T> createFinancialTransactions(data: T): List<FinancialTransaction>

    fun apply(block: TransactionProcessor.() -> Unit): TransactionProcessor {
        this.block()
        return this
    }

}