package dev.wo.domain.services

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.models.ofx.FinancialTransaction
import dev.wo.domain.models.ofx.OFXFile
import java.io.File

interface TransactionProcessor {
    val institution: FinancialInstitution
    var fileType: String?
    var file: File?
    fun processFile()
    fun createFinancialTransactions(data: OFXFile): MutableList<FinancialTransaction>
    fun withFileType(fileType: String) = fileType.also { this.fileType = it }
    fun withFile(file: File?) = file.also { this.file = it }
}