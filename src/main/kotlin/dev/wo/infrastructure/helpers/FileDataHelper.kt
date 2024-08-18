package dev.wo.infrastructure.helpers

import dev.wo.domain.enums.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object FileDataHelper {
    fun getDate(date: String?, pattern: String = "yyyyMMddHHmmss"): LocalDateTime? {
        date ?: return null
        val inputWithoutTimeZone = date.replace("\\[.*\\]".toRegex(), "")
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return try {
            LocalDateTime.parse(inputWithoutTimeZone, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun getTransactionType(type: String?): TransactionType {
        type ?: return TransactionType.UNKNOWN
        val transactionTypeAsString =  when (type) {
            "CREDIT" -> "Crédito"
            "DEBIT" -> "Débito"
            "PIX" -> "Pix"
            else -> type
        }

        return TransactionType.fromString(transactionTypeAsString)
    }

    fun generateUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
