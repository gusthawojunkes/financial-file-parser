package dev.wo.domain.services

import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.domain.models.ofx.OFXFile
import java.io.File

interface TransactionProcessor {
    var fileType: String?
    var file: File?

    @Throws(FileProcessingException::class)
    fun processFile(): MutableList<FinancialTransaction>

    fun createFinancialTransactions(data: OFXFile): MutableList<FinancialTransaction>

    fun withFileType(fileType: String) = fileType.also { this.fileType = it }

    fun withFile(file: File?) = file.also { this.file = it }
}