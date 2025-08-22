package dev.wo.infrastructure.metrics

import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.exceptions.HttpException
import io.ktor.http.HttpStatusCode
import io.micrometer.core.instrument.Counter
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.slf4j.LoggerFactory

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
            id = "file_processing_errors_total",
            description = "Total number of errors during file processing",
            tags = mapOf(
                "error_type" to exception.status.description,
                "error_code" to exception.status.value.toString()
            )
        )
    }

    fun incrementError(exception: FileProcessingException) {
        increment(
            id = "file_processing_errors_total",
            description = "Total number of errors during file processing",
            tags = mapOf(
                "error_type" to exception.status.description,
                "error_code" to exception.status.value.toString()
            )
        )
    }

    fun incrementError(exception: Exception) {
        increment(
            id = "file_processing_errors_total",
            description = "Total number of errors during file processing",
            tags = mapOf(
                "error_type" to HttpStatusCode.InternalServerError.description,
                "error_code" to HttpStatusCode.InternalServerError.value.toString()
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