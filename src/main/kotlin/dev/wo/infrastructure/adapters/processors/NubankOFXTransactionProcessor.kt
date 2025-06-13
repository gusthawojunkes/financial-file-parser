package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.services.ProcessorConfiguration
import java.io.File

class NubankOFXTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorConfiguration? = null
) : CommonOFXTransactionProcessor(file, preferences, FinancialInstitution.NUBANK) {
}