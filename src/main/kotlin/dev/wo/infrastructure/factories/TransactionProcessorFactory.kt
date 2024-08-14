package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.services.TransactionProcessor
import dev.wo.infrastructure.adapters.processors.NubankOFXTransactionProcessor

object TransactionProcessorFactory {
    fun getProcessor(institution: FinancialInstitution, fileType: String): TransactionProcessor {
        return when (institution) {
            FinancialInstitution.NUBANK -> when (fileType.lowercase()) {
                "ofx" -> NubankOFXTransactionProcessor()
                else -> throw FileProcessingException("Invalid file type for Nubank")
            }
            else -> throw FileProcessingException("Invalid financial institution")
        }
    }
}