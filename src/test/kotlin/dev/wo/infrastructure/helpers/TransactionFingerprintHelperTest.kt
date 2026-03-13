package dev.wo.infrastructure.helpers

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TransactionFingerprintHelperTest {

    private val baseTime = LocalDateTime.of(2024, 4, 2, 0, 0, 0)

    @Test
    fun `should generate the same id for the same transaction data`() {
        val id1 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Mercado Livre",
            currency = "BRL"
        )
        val id2 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Mercado Livre",
            currency = "BRL"
        )

        assertEquals(id1, id2, "Reimportação do mesmo arquivo deve gerar o mesmo ID")
    }

    @Test
    fun `should generate different ids for transactions with different values`() {
        val id1 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Mercado Livre"
        )
        val id2 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -75.0,
            transactionTime = baseTime,
            description = "Mercado Livre"
        )

        assertNotEquals(id1, id2)
    }

    @Test
    fun `should generate different ids for transactions with different descriptions (parcelas)`() {
        val id1 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Compra - Parcela 1/3"
        )
        val id2 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Compra - Parcela 2/3"
        )

        assertNotEquals(id1, id2, "Parcelas diferentes devem ter IDs diferentes")
    }

    @Test
    fun `should generate different ids for transactions with different datetime`() {
        val id1 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Ifood"
        )
        val id2 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime.plusHours(2),
            description = "Ifood"
        )

        assertNotEquals(id1, id2, "Mesma transação em horários diferentes deve ter IDs diferentes")
    }

    @Test
    fun `should generate different ids for transactions from different institutions`() {
        val id1 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Ifood"
        )
        val id2 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.C6_BANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Ifood"
        )

        assertNotEquals(id1, id2)
    }

    @Test
    fun `should normalize description whitespace before hashing`() {
        val id1 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "  Mercado   Livre  "
        )
        val id2 = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Mercado Livre"
        )

        assertEquals(id1, id2, "Descrições com espaços extras devem gerar o mesmo ID após normalização")
    }

    @Test
    fun `should generate a 64-character sha256 hex string`() {
        val id = TransactionFingerprintHelper.generate(
            institution = FinancialInstitution.NUBANK,
            transactionType = TransactionType.DEBIT,
            value = -50.0,
            transactionTime = baseTime,
            description = "Teste"
        )

        assertEquals(64, id.length)
        assert(id.matches(Regex("[0-9a-f]+"))) { "ID deve ser hexadecimal lowercase" }
    }
}

