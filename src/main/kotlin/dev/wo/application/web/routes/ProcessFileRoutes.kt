package dev.wo.application.web.routes

import dev.wo.application.web.resource.response.FinancialTransactionResponse
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.infrastructure.factories.TransactionProcessorFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger(Route::class.java)

private suspend fun ApplicationCall.getRequiredHeader(headerName: String): String? {
    return request.headers[headerName] ?: run {
        respond(HttpStatusCode.BadRequest, "$headerName header is required")
        null
    }
}

private suspend fun ApplicationCall.getTempFile(): File {
    val tempFile = withContext(Dispatchers.IO) {
        File.createTempFile("upload_", ".tmp")
    }
    receiveStream().use { inputStream ->
        tempFile.outputStream().buffered().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return tempFile
}

fun Route.processFileRoutes() {
    route("api/v1") {
        route("file") {
            post("process") {
                var tempFile: File? = null
                val transactions: List<FinancialTransactionResponse>
                val institution = call.getRequiredHeader("Institution") ?: return@post
                val fileType = call.getRequiredHeader("File-Type") ?: return@post

                logger.debug("Processing $institution file with file type: $fileType")

                try {
                    tempFile = call.getTempFile()
                    val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.fromString(institution))
                    processor.withFile(tempFile)
                    processor.withFileType(fileType)
                    transactions = processor.processFile().map { FinancialTransactionResponse.from(it) }
                    if (transactions.isEmpty()) {
                        call.respond(HttpStatusCode.NoContent, "No transactions found")
                        return@post
                    }

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