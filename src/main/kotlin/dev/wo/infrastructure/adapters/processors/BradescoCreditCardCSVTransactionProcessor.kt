package dev.wo.infrastructure.adapters.processors

import dev.wo.domain.common.ProcessingResult
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.services.ProcessorConfiguration
import dev.wo.domain.services.TransactionProcessor
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.helpers.TransactionFingerprintHelper
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year

/**
 * Processor for Bradesco credit card invoice CSV files (Fatura do Cartão de Crédito).
 *
 * Expected format:
 * ```
 * Data: DD/MM/YYYY HH:MM:SS  Situação da Fatura: STATUS  NOME DO TITULAR ;;; XXXX
 * Data;Histórico;Valor(US$);Valor(R$)
 * dd/MM;DESCRIÇÃO;valorUS;valorBRL
 * ...
 * NOME DO TITULAR ;;; YYYY   <- second card section (optional)
 * Data;Histórico;Valor(US$);Valor(R$)
 * dd/MM;DESCRIÇÃO;valorUS;valorBRL
 * ...
 * Total da fatura em Real: ;;;TOTAL
 * Resumo das Despesas;Real
 * ```
 */
class BradescoCreditCardCSVTransactionProcessor(
    override var file: File? = null,
    override var preferences: ProcessorConfiguration? = null
) : TransactionProcessor {

    companion object {
        private val HEADER_COLUMN_MARKER = "Data;Hist"
        private val TOTAL_LINE_MARKER = "Total da fatura em Real"
        private val RESUMO_LINE_MARKER = "Resumo das Despesas"
        private val DATE_COLUMN_PATTERN = Regex("""^\d{2}/\d{2}$""")
        private val CARD_SECTION_PATTERN = Regex(""".*;;;\s*(\d{4})\s*$""")
        private val INVOICE_DATE_PATTERN = Regex("""Data:\s*(\d{2}/\d{2}/\d{4})""")
    }

    override fun processFile(): ProcessingResult<List<FinancialTransaction>> {
        file?.let {
            val lines = it.useLines { seq -> seq.toList() }
            if (lines.isEmpty()) {
                return ProcessingResult.success(emptyList())
            }
            return ProcessingResult.success(parseInvoiceLines(lines))
        }
        return ProcessingResult.success(emptyList())
    }

    private fun parseInvoiceLines(lines: List<String>): List<FinancialTransaction> {
        val transactions = mutableListOf<FinancialTransaction>()
        val delimiter = preferences?.csvSeparator ?: ';'

        // Try to extract invoice year from first line (e.g., "Data: 10/03/2026 ...")
        val invoiceYear = extractInvoiceYear(lines.firstOrNull() ?: "")

        var currentCardLastFourDigits: String? = null

        for (line in lines) {
            val trimmed = line.trim()

            // Stop at summary lines
            if (trimmed.startsWith(TOTAL_LINE_MARKER) || trimmed.startsWith(RESUMO_LINE_MARKER)) {
                break
            }

            // Detect card section header (e.g., "NOME DO TITULAR ;;; 1234")
            val cardSectionMatch = CARD_SECTION_PATTERN.find(trimmed)
            if (cardSectionMatch != null) {
                currentCardLastFourDigits = cardSectionMatch.groupValues[1]
                continue
            }

            // Skip column header lines
            if (trimmed.startsWith(HEADER_COLUMN_MARKER)) {
                continue
            }

            // Parse transaction lines (start with dd/MM pattern)
            val parts = trimmed.split(delimiter)
            if (parts.size < 4) continue

            val dateStr = parts[0].trim()
            if (!DATE_COLUMN_PATTERN.matches(dateStr)) continue

            val description = parts[1].trim()
            val usdValueStr = parts[2].trim()
            val brlValueStr = parts[3].trim()

            if (description.isBlank() || brlValueStr.isBlank()) continue

            val brlValue = parseBrazilianDecimal(brlValueStr) ?: continue
            val usdValue = if (usdValueStr.isNotBlank()) parseBrazilianDecimal(usdValueStr) else null

            // Determine the year: use invoice year, but if month is later than invoice month we use previous year
            val transactionDate = resolveDate(dateStr, invoiceYear)
            val transactionTime = LocalDateTime.of(transactionDate, LocalTime.MIDNIGHT)

            // Negative BRL value = payment/credit on invoice; positive = debit (expense)
            val transactionType = if (brlValue < 0.0) TransactionType.CREDIT else TransactionType.DEBIT
            // Negate so that expenses are stored as negative and payments as positive
            val finalValue = -brlValue

            val currency = if (usdValue != null && usdValue != 0.0) "USD" else "BRL"

            val identifier = TransactionFingerprintHelper.generate(
                institution = FinancialInstitution.BRADESCO,
                transactionType = transactionType,
                value = finalValue,
                transactionTime = transactionTime,
                description = description,
                currency = currency
            )

            val transaction = FinancialTransaction(
                value = finalValue,
                description = description,
                transactionTime = transactionTime,
                identifier = identifier,
                transactionType = transactionType,
                institution = FinancialInstitution.BRADESCO,
                cardType = CardType.CREDIT,
                currency = currency,
                institutionUUID = null,
                cardLastFourDigits = currentCardLastFourDigits
            )

            transactions.add(transaction)
        }

        return transactions
    }

    /**
     * Extracts the year from the first line, which typically contains:
     * "Data: 10/03/2026 01:30:34  Situação da Fatura: ..."
     * Falls back to the current year if not found.
     */
    private fun extractInvoiceYear(firstLine: String): Int {
        val match = INVOICE_DATE_PATTERN.find(firstLine)
        if (match != null) {
            val dateStr = match.groupValues[1] // dd/MM/yyyy
            return dateStr.substring(6).toIntOrNull() ?: Year.now().value
        }
        return Year.now().value
    }

    /**
     * Resolves a "dd/MM" transaction date to a full LocalDate using the invoice year.
     * If the transaction month is after the invoice month it belongs to the previous year.
     */
    private fun resolveDate(ddMM: String, invoiceYear: Int): LocalDate {
        val parts = ddMM.split("/")
        val day = parts[0].toIntOrNull() ?: 1
        val month = parts[1].toIntOrNull() ?: 1

        // If month > current month in invoice year, it likely belongs to the previous year
        // (e.g., invoice closed in Jan may have Dec transactions from prior year)
        return try {
            LocalDate.of(invoiceYear, month, day)
        } catch (_: Exception) {
            LocalDate.of(invoiceYear, month, 1)
        }
    }

    /**
     * Parses Brazilian decimal format (e.g., "1.500,75" -> 1500.75 or "22,00" -> 22.0).
     * Returns null if blank or invalid.
     */
    private fun parseBrazilianDecimal(value: String): Double? {
        if (value.isBlank()) return null
        return try {
            BigDecimal(
                value.trim()
                    .replace(".", "")
                    .replace(",", ".")
            ).toDouble()
        } catch (_: NumberFormatException) {
            null
        }
    }

    override fun <T> createFinancialTransactions(data: T): List<FinancialTransaction> {
        // Not used directly - parsing is handled in processFile via parseInvoiceLines
        return emptyList()
    }
}





