package dev.wo.domain.enums

enum class TransactionType (val description: String) {
    CREDIT("Crédito"),
    DEBIT("Débito"),
    PIX("Pix"),
    UNKNOWN("Desconhecido");

    companion object {
        fun fromString(name: String): TransactionType {
            return TransactionType.entries.find { it.description.equals(name, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
