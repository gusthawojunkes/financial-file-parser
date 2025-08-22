package dev.wo.infrastructure.metrics

import io.ktor.http.HttpStatusCode
import io.micrometer.core.instrument.Counter
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.slf4j.LoggerFactory

class MetricsGenerator(private val registry: PrometheusMeterRegistry) {

    private val logger = LoggerFactory.getLogger(MetricsGenerator::class.java)

    fun incrementFileProcessingApiCalls() {
        increment("file_processing_calls_total", "Total number of file processing API calls")
    }

    fun increment(id: String, description: String) {
        logger.info("Incrementing metrics for id={}", id)
        Counter.builder(id)
            .description(description)
            .register(registry)
            .increment()
    }

    fun incrementInstitutionCounter(institution: String) {
        logger.info("Incrementing institution counter for institution={}", institution)
        Counter.builder("file_processing_requests_by_institution_total")
            .description("Total number of file processing requests by institution")
            .tag("institution", institution)
            .register(registry)
            .increment()
    }

    fun incrementStatusCodeError(status: HttpStatusCode) {
        logger.info("Incrementing error counter for statusCode=({}) {}", status.value, status.description)
        Counter.builder("file_processing_errors_total")
            .description("Total number of errors during file processing")
            .tag("error_type", status.description)
            .tag("error_code", status.value.toString())
            .register(registry)
            .increment()
    }
}