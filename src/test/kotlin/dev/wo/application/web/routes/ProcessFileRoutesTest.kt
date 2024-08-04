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

class ProcessFileRoutesTest {

    @Test
    fun `processing a ofx file from nubank`() = testApplication {
        val fileBytes = File("src/test/resources/files/nubank.ofx").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Nubank")
                append("File-Type", "OFX")
            }
            setBody(fileBytes)
        }
        val type = object : TypeToken<MutableList<FinancialTransaction>>() {}.type
        val transactions: MutableList<FinancialTransaction> =  GsonProvider.fromJson(response.bodyAsText(), type)
        val firstTransaction = transactions.first()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(5, transactions.size)
        assertEquals(FinancialInstitution.NUBANK, firstTransaction.institution)
        assertEquals(TransactionType.DEBIT, firstTransaction.transactionType)
        assertEquals(CardType.CREDIT, firstTransaction.cardType)
        assertNotNull(firstTransaction.value)
        assertTrue(firstTransaction.value.toInt() != 0)
    }


    @Test
    fun `processing a file type that is not implemented in NubankTransactionProcessor`() = testApplication {
        val fileBytes = File("src/test/resources/files/nubank.ofx").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Nubank")
                append("File-Type", "NotImplemented")
            }
            setBody(fileBytes)
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Invalid file type", response.bodyAsText())
    }

    @Test
    fun `processing a file without Institution required header`() = testApplication {
        val fileBytes = File("src/test/resources/files/nubank.ofx").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("File-Type", "OFX")
            }
            setBody(fileBytes)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Institution header is required", response.bodyAsText())
    }

    @Test
    fun `processing a file without File-Type required header`() = testApplication {
        val fileBytes = File("src/test/resources/files/nubank.ofx").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Nubank")
            }
            setBody(fileBytes)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("File-Type header is required", response.bodyAsText())
    }

    @Test
    fun `processing a file with invalid financial institution`() = testApplication {
        val fileBytes = File("src/test/resources/files/nubank.ofx").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Invalid")
                append("File-Type", "OFX")
            }
            setBody(fileBytes)
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Invalid financial institution", response.bodyAsText())
    }

    @Test
    fun `processing a file with no transactions found`() = testApplication {
        val fileBytes = File("src/test/resources/files/empty.ofx").readBytes()
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Nubank")
                append("File-Type", "OFX")
            }
            setBody(fileBytes)
        }
        assertEquals(HttpStatusCode.NoContent, response.status)
        assertEquals("No transactions found", response.bodyAsText())
    }

}