package dev.wo.infrastructure.factories

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.FinancialInstitution.NUBANK
import dev.wo.domain.services.TransactionProcessor
import dev.wo.infrastructure.adapters.processors.NubankTransactionProcessor

class TransactionProcessorFactory {

    companion object {
        fun getProcessor(institution: FinancialInstitution): TransactionProcessor {
            return when (institution) {
                NUBANK -> NubankTransactionProcessor(NUBANK)
                else -> throw IllegalArgumentException("Invalid financial institution")
            }
        }
    }
}