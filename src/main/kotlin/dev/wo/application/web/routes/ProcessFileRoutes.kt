package dev.wo.application.web.routes

import dev.wo.application.web.resource.response.FinancialTransactionResponse
import dev.wo.domain.enums.FinancialInstitution
import dev.wo.domain.exceptions.FileProcessingException
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
import java.util.UUID

private val logger = LoggerFactory.getLogger(Route::class.java)

fun Route.fileRouting() {
    val metrics by inject<MetricsGenerator>()

    route("api/v1") {
        route("file") {
            post("process") {
                metrics.incrementApiCall()

                val requestUuid = UUID.randomUUID().toString();

                var tempFile: File? = null
                var transactions: List<FinancialTransactionResponse>
                try {
                    val institution = call.getRequiredHeader("Institution")
                    metrics.incrementInstitution(institution)

                    val fileType = call.getRequiredHeader("File-Type")
                    val preferences = call.getPreferences()

                    logger.info("[$requestUuid] Processing $institution file with $fileType file type")

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
                        logger.info("[$requestUuid] Transactions size: {}", it.size)
                    }

                } catch (exception: HttpException) {
                    logger.error("[$requestUuid] Error processing file", exception)
                    metrics.incrementError(exception)
                    call.respond(exception.status, exception.message)
                    return@post
                } catch (exception: FileProcessingException) {
                    logger.error("[$requestUuid] Error processing file", exception)
                    metrics.incrementError(exception)
                    call.respond(exception.status, exception.message)
                    return@post
                } catch (exception: Exception) {
                    logger.error("[$requestUuid] Error processing file", exception)
                    metrics.incrementError(exception)
                    call.respond(HttpStatusCode.InternalServerError, exception.message ?: "Error processing file")
                    return@post
                } finally {
                    tempFile?.deleteOnExit()
                }

                logger.info("[$requestUuid] Processing finished")
                call.respond(HttpStatusCode.OK, transactions)
            }
        }
    }
}