package dev.wo.application.web.routes

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessFileRoutesTest {

    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun testProcessFile() = testApplication {
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
    }


}