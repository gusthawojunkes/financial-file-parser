package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.FinancialInstitution.NUBANK
import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.services.TransactionProcessor
import dev.wo.infrastructure.adapters.processors.NubankTransactionProcessor

class TransactionProcessorFactory {
    companion object {
        fun getProcessor(institution: FinancialInstitution): TransactionProcessor {
            return when (institution) {
                NUBANK -> NubankTransactionProcessor()
                else -> throw FileProcessingException("Invalid financial institution")
            }
        }
    }
}