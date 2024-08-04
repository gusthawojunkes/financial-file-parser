package dev.wo.application.web.routes

import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.transactions.FinancialTransaction
import dev.wo.infrastructure.factories.TransactionProcessorFactory
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(Route::class.java)

private suspend fun ApplicationCall.getRequiredHeader(headerName: String): String? {
    return request.headers[headerName] ?: run {
        respond(HttpStatusCode.BadRequest, "$headerName header is required")
        null
    }
}

fun Route.processFileRoutes() {
    route("api/v1") {
        route("file") {
            post("process") {
                val file = call.receiveMultipart()
                var tempFile: File? = null
                val transactions: MutableList<FinancialTransaction>
                val institution = call.getRequiredHeader("Institution") ?: return@post
                val fileType = call.getRequiredHeader("File-Type") ?: return@post

                logger.debug("Processing file for institution: $institution and file type: $fileType")

                try {
                    file.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            tempFile = File.createTempFile("upload_", part.originalFileName?.replace(" ", "_") ?: "tempfile")
                            part.streamProvider().use { inputStream ->
                                tempFile!!.outputStream().buffered().use {
                                    inputStream.copyTo(it)
                                }
                            }
                        }
                        part.dispose()
                    }

                    val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.fromString(institution))
                    processor.withFile(tempFile)
                    processor.withFileType(fileType)
                    transactions = processor.processFile()
                } catch (e: Exception) {
                    logger.error("Error processing file", e)
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Error processing file")
                    return@post
                } finally {
                    tempFile?.deleteOnExit()
                }

                logger.debug("Transactions size: {}", transactions.size)
                logger.debug("Processing finished")
                call.respond(HttpStatusCode.OK, transactions)
            }
        }
    }
}