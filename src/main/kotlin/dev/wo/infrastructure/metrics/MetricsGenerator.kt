package dev.wo.infrastructure.metrics

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
}