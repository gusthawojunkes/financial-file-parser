package dev.wo.infrastructure.metrics

import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.exceptions.HttpException
import io.ktor.http.HttpStatusCode
import io.micrometer.core.instrument.Counter
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.slf4j.LoggerFactory

private const val INCREMENT_ERROR_DESCRIPTION = "Total number of errors during file processing"

private const val FILE_PROCESSING_ERRORS_TOTAL = "file_processing_errors_total"

private const val ERROR_TYPE_TAG = "error_type"
private const val ERROR_CODE_TAG = "error_code"

class MetricsGenerator(private val registry: PrometheusMeterRegistry) {

    private val logger = LoggerFactory.getLogger(MetricsGenerator::class.java)

    fun incrementApiCall() {
        increment(
            id = "file_processing_calls_total",
            description = "Total number of file processing API calls"
        )
    }

    fun incrementInstitution(institution: String) {
        increment(
            id = "file_processing_requests_by_institution_total",
            description = "Total number of file processing requests by institution",
            tags = mapOf("institution" to institution)
        )
    }

    fun incrementError(exception: HttpException) {
        increment(
            id = FILE_PROCESSING_ERRORS_TOTAL,
            description = INCREMENT_ERROR_DESCRIPTION,
            tags = mapOf(
                ERROR_TYPE_TAG to exception.status.description,
                ERROR_CODE_TAG to exception.status.value.toString()
            )
        )
    }

    fun incrementError(exception: FileProcessingException) {
        increment(
            id = FILE_PROCESSING_ERRORS_TOTAL,
            description = INCREMENT_ERROR_DESCRIPTION,
            tags = mapOf(
                ERROR_TYPE_TAG to exception.status.description,
                ERROR_CODE_TAG to exception.status.value.toString()
            )
        )
    }

    fun incrementError(exception: Exception) {
        increment(
            id = FILE_PROCESSING_ERRORS_TOTAL,
            description = INCREMENT_ERROR_DESCRIPTION,
            tags = mapOf(
                ERROR_TYPE_TAG to HttpStatusCode.InternalServerError.description,
                ERROR_CODE_TAG to HttpStatusCode.InternalServerError.value.toString()
            )
        )
    }

    fun increment(id: String, description: String, tags: Map<String, String> = emptyMap()) {
        logger.info("Incrementing metrics for id={}", id)
        val builder = Counter.builder(id).description(description)
        tags.forEach { (key, value) ->
            builder.tag(key, value)
        }
        builder.register(registry).increment()
    }

}