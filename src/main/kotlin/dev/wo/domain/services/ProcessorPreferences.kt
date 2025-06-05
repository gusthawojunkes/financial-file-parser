package dev.wo.domain.services

import dev.wo.domain.enums.InvoiceType

data class ProcessorConfiguration(
    val csvSeparator: Char = ',',
    val dateTimePattern: String = "dd/MM/yyyy HH:mm:ss",
    var invoiceType: InvoiceType = InvoiceType.UNKNOWN
)
