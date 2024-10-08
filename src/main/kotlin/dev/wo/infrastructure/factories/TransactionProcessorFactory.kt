package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.services.TransactionProcessor
import dev.wo.infrastructure.adapters.processors.CommonCSVTransactionProcessor
import dev.wo.infrastructure.adapters.processors.NubankOFXTransactionProcessor

object TransactionProcessorFactory {
    fun getProcessor(institution: FinancialInstitution, fileType: String): TransactionProcessor {
        val processorFileType = fileType.lowercase()

        return when (institution) {
            FinancialInstitution.NUBANK -> when (processorFileType) {
                "ofx" -> NubankOFXTransactionProcessor()
                else -> throw FileProcessingException("Invalid file type for Nubank")
            }
            FinancialInstitution.ANY -> when (processorFileType) {
                "csv" -> CommonCSVTransactionProcessor()
                else -> throw FileProcessingException("Invalid file type for Any")
            }
            else -> throw FileProcessingException("Invalid financial institution")
        }
    }
}