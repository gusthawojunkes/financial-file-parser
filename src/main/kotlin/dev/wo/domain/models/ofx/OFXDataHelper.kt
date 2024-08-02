package dev.wo.domain.models.ofx

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class OFXDataHelper {
    companion object {
        fun getDate(date: String): LocalDateTime? {
            val inputWithoutTimeZone = date.replace("\\[.*\\]".toRegex(), "")
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            return try {
                LocalDateTime.parse(inputWithoutTimeZone, formatter)
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }
}
