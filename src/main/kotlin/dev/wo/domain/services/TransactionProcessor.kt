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

    fun withFile(file: File?) = file.also { this.file = it }

    fun withPreferences(preferences: ProcessorConfiguration?) = preferences.also { this.preferences = it }

}