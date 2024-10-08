package dev.wo.domain.services

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.domain.models.ofx.OFXFile
import java.io.File

interface TransactionProcessor {
    var file: File?
    var preferences: ProcessorPreferences?

    @Throws(FileProcessingException::class)
    fun processFile(): ProcessingResult<List<FinancialTransaction>>

    fun <T> createFinancialTransactions(data: T): List<FinancialTransaction>

    fun withFile(file: File?) = file.also { this.file = it }

    fun withPreferences(preferences: ProcessorPreferences?) = preferences.also { this.preferences = it }

}