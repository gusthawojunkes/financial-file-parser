package dev.wo.domain.enums

enum class FinancialInstitution (val description: String) {

    NUBANK("Nubank"),
    ITAU("Itaú"),
    C6_BANK("C6 Bank"),
    ANY("Any"),
    UNKNOWN("Desconhecida");

    companion object {
        fun fromString(name: String): FinancialInstitution {
            return entries.find { it.description.equals(name, ignoreCase = true) } ?: UNKNOWN
        }
    }

}