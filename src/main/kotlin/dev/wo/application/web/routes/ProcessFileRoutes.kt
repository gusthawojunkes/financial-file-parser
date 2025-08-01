package dev.wo.application.web.routes

import dev.wo.application.web.resource.response.FinancialTransactionResponse
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.HttpException
import dev.wo.infrastructure.adapters.FileService
import dev.wo.infrastructure.adapters.getPreferences
import dev.wo.infrastructure.adapters.getRequiredHeader
import dev.wo.infrastructure.adapters.getTempFile
import dev.wo.infrastructure.factories.TransactionProcessorFactory
import dev.wo.infrastructure.metrics.MetricsGenerator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger(Route::class.java)

fun Route.fileRouting() {
    val metricsGenerator by inject<MetricsGenerator>()

    route("api/v1") {
        route("file") {
            post("process") {
                metricsGenerator.incrementFileProcessingApiCalls()

                var tempFile: File? = null
                var transactions: List<FinancialTransactionResponse>
                try {
                    val institution = call.getRequiredHeader("Institution")
                    val fileType = call.getRequiredHeader("File-Type")
                    val preferences = call.getPreferences()

                    logger.debug("Processing $institution file with $fileType file type")

                    tempFile = call.getTempFile()
                    val isFileTypeValid = FileService.validateFileType(fileType)

                    if (!isFileTypeValid) {
                        throw HttpException(HttpStatusCode.UnsupportedMediaType, "Invalid file type")
                    }

                    val processor = TransactionProcessorFactory.getProcessor(FinancialInstitution.fromString(institution), fileType)
                    processor.withFile(tempFile)
                    processor.withPreferences(preferences)
                    transactions = processor.processFile().let {
                        if (it.data == null || it.data.isEmpty()) {
                            throw HttpException(HttpStatusCode.NoContent, "No transactions found")
                        }

                        it.data.map { FinancialTransactionResponse.from(it) }
                    }.also {
                        logger.debug("Transactions size: {}", it.size)
                    }

                } catch (httpException: HttpException) {
                    logger.error("Error processing file", httpException)
                    call.respond(httpException.status, httpException.message)
                    return@post
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, exception.message ?: "Error processing file")
                    return@post
                } finally {
                    tempFile?.deleteOnExit()
                }

                logger.debug("Processing finished")
                call.respond(HttpStatusCode.OK, transactions)
            }
        }
    }
}