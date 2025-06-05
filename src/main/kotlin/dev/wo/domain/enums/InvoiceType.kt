package dev.wo.domain.enums

enum class InvoiceType(val description: String) {
    CREDIT_INVOICE("CREDIT_INVOICE"),
    ACCOUNT_STATEMENT("ACCOUNT_STATEMENT"),
    UNKNOWN("DESCONHECIDO");

    companion object {
        fun fromString(name: String): InvoiceType {
            return InvoiceType.entries.find { it.description.equals(name, ignoreCase = true) } ?: UNKNOWN
        }
    }
}