package dev.wo.application.web.routes

import com.google.gson.reflect.TypeToken
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.serialization.GsonProvider
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProcessCommonCSVFilesRouteTest {

    @Test
    fun `processing a generic csv file for C6 Bank`() = testApplication {
        val fileBytes = File("src/test/resources/files/csv/c6_comma.csv").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Any")
                append("File-Type", "CSV")
                append("CSV-Separator", ",")
                append("Invoice-Type", "CREDIT_INVOICE")
            }
            setBody(fileBytes)
        }
        val type = object : TypeToken<MutableList<FinancialTransaction>>() {}.type
        val transactions: MutableList<FinancialTransaction> =  GsonProvider.fromJson(response.bodyAsText(), type)
        val firstTransaction = transactions.first()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(firstTransaction)
        assertNotNull(firstTransaction.value)
        assertTrue(transactions.isNotEmpty())
        assertTrue(firstTransaction.value.toDouble() != 0.0)
        assertEquals(FinancialInstitution.C6_BANK, firstTransaction.institution)
        assertEquals(TransactionType.DEBIT, firstTransaction.transactionType)
        assertEquals(CardType.CREDIT, firstTransaction.cardType)
    }

    @Test
    fun `processing a generic csv file for C6 Bank with semicolon separator`() = testApplication {
        val fileBytes = File("src/test/resources/files/csv/c6_semicolon.csv").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Any")
                append("File-Type", "CSV")
                append("CSV-Separator", ";")
                append("Invoice-Type", "CREDIT_INVOICE")
            }
            setBody(fileBytes)
        }
        val type = object : TypeToken<MutableList<FinancialTransaction>>() {}.type
        val transactions: MutableList<FinancialTransaction> =  GsonProvider.fromJson(response.bodyAsText(), type)
        val firstTransaction = transactions.first()

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(firstTransaction)
        assertNotNull(firstTransaction.value)
        assertTrue(transactions.isNotEmpty())
        assertTrue(firstTransaction.value.toDouble() != 0.0)
        assertEquals(FinancialInstitution.C6_BANK, firstTransaction.institution)
        assertEquals(TransactionType.DEBIT, firstTransaction.transactionType)
        assertEquals(CardType.CREDIT, firstTransaction.cardType)
    }

}