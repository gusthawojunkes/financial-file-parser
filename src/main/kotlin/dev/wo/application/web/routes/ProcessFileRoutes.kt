package dev.wo.application.web.routes

import dev.wo.application.web.resource.response.FinancialTransactionResponse
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.HttpException
import dev.wo.infrastructure.adapters.FileService
import dev.wo.infrastructure.adapters.getRequiredHeader
import dev.wo.infrastructure.adapters.getTempFile
import dev.wo.infrastructure.factories.TransactionProcessorFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger(Route::class.java)

fun Route.fileRouting() {
    route("api/v1") {
        route("file") {
            post("process") {
                var tempFile: File? = null
                val transactions: List<FinancialTransactionResponse>

                try {
                    val institution = call.getRequiredHeader("Institution")
                    val fileType = call.getRequiredHeader("File-Type")

                    logger.debug("Processing $institution file with $fileType file type")

                    tempFile = call.getTempFile()
                    val isFileTypeValid = FileService.validateFileType(tempFile, fileType)

                    if (!isFileTypeValid) {
                        throw HttpException(HttpStatusCode.UnsupportedMediaType, "Invalid file type")
                    }

                    val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.fromString(institution))
                    processor.withFile(tempFile)
                    processor.withFileType(fileType)
                    transactions = processor.processFile().map { FinancialTransactionResponse.from(it) }

                    if (transactions.isEmpty()) {
                        throw HttpException(HttpStatusCode.NoContent, "No transactions found")
                    }

                } catch (httpException: HttpException) {
                    logger.error("Error processing file", httpException)
                    call.respond(httpException.status, httpException.message)
                    return@post
                } catch (e: Exception) {
                    e.printStackTrace()
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