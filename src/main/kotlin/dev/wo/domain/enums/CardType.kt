package dev.wo.domain.enums

enum class CardType (
    val description: String
) {
    CREDIT("Crédito"),
    DEBIT("Débito"),
    NONE("N/A");

    companion object {
        fun fromString(name: String): CardType {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: NONE
        }
    }
}