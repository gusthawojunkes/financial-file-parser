package dev.wo.application.web.routes

import dev.wo.application.services.ProcessFileService
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
import io.ktor.server.http.content.file
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.UUID

private val logger = LoggerFactory.getLogger(Route::class.java)

fun Route.fileRouting() {
    val metrics by inject<MetricsGenerator>()
    val service by inject<ProcessFileService>()

    route("api/v1/file") {
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

                transactions = service.process(
                    institution = FinancialInstitution.fromString(institution),
                    fileType = fileType,
                    file = tempFile,
                    preferences = preferences
                ).also {
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