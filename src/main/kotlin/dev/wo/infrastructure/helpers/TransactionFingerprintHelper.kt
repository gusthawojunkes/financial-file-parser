package dev.wo.infrastructure.helpers

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TransactionFingerprintHelper {

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

    fun generate(
        institution: FinancialInstitution,
        transactionType: TransactionType,
        value: Double,
        transactionTime: LocalDateTime,
        description: String,
        currency: String = "BRL",
        account: String? = null
    ): String {
        val normalizedValue = BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString()
        val normalizedDescription = description.trim().lowercase().replace("\\s+".toRegex(), " ")
        val normalizedTime = transactionTime.format(DATE_FORMATTER)

        val fingerprint = buildString {
            append(institution.name)
            append("|")
            append(transactionType.name)
            append("|")
            append(normalizedValue)
            append("|")
            append(normalizedTime)
            append("|")
            append(normalizedDescription)
            append("|")
            append(currency.uppercase())
            if (!account.isNullOrBlank()) {
                append("|")
                append(account.trim())
            }
        }

        return sha256(fingerprint)
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

