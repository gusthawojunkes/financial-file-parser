package dev.wo.application.web.routes

import com.google.gson.reflect.TypeToken
import dev.wo.domain.enums.CardType
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.enums.TransactionType
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.serialization.GsonProvider
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertTrue
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProcessFileRoutesTest {

    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun `processing a ofx file from nubank`() = testApplication {
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Nubank")
                append("File-Type", "OFX")
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("description", "OFX file")
                        append("file", File("src/test/resources/files/nubank.ofx").readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
                            append(HttpHeaders.ContentDisposition, "filename=\"nubank.ofx\"")
                        })

                    }
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val type = object : TypeToken<MutableList<FinancialTransaction>>() {}.type
        val transactions: MutableList<FinancialTransaction> =  GsonProvider.fromJson(response.bodyAsText(), type)
        val firstTransaction = transactions.first()

        assertEquals(5, transactions.size)
        assertEquals(FinancialInstitution.NUBANK, firstTransaction.institution)
        assertEquals(TransactionType.DEBIT, firstTransaction.transactionType)
        assertEquals(CardType.CREDIT, firstTransaction.cardType)
        assertNotNull(firstTransaction.value)
        assertTrue(firstTransaction.value.toInt() != 0)
    }


    @Test
    fun `processing a file type that is not implemented in NubankTransactionProcessor`() = testApplication {
        val response = client.post("/api/v1/file/process") {
            headers {
                append("Institution", "Nubank")
                append("File-Type", "NotImplemented")
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("description", "OFX file")
                        append("file", File("src/test/resources/files/nubank.ofx").readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
                            append(HttpHeaders.ContentDisposition, "filename=\"nubank.ofx\"")
                        })

                    }
                )
            )
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Invalid file type", response.bodyAsText())
    }

}