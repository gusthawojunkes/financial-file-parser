package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.services.TransactionProcessor

class NubankTransactionProcessor(
    override val institution: FinancialInstitution
) : TransactionProcessor {

    override fun processOFX() {
        TODO("Not yet implemented")
    }

}
