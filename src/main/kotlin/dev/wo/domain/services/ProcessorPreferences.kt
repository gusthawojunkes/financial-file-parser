package dev.wo.domain.services

data class ProcessorPreferences(
    val csvSeparator: Char = ',',
    val dateTimePattern: String = "dd/MM/yyyy HH:mm:ss",
)
