package dev.wo.domain.services

import dev.wo.domain.enums.FinancialInstitution

interface TransactionProcessor {
    val institution: FinancialInstitution
    fun processOFX()
}