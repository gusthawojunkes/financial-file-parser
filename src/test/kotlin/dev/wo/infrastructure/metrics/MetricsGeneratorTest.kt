package dev.wo.infrastructure.metrics

import dev.wo.domain.exceptions.FileProcessingException
import dev.wo.domain.exceptions.HttpException
import io.ktor.http.HttpStatusCode
import io.micrometer.core.instrument.Counter
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class MetricsGeneratorTest {

    private val registry = mockk<PrometheusMeterRegistry>(relaxed = true)
    private val metricsGenerator = MetricsGenerator(registry)
    private val counterBuilder = mockk<Counter.Builder>(relaxed = true)
    private val counter = mockk<Counter>(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(Counter::class)
        every { Counter.builder(any()) } returns counterBuilder
        every { counterBuilder.description(any()) } returns counterBuilder
        every { counterBuilder.tag(any(), any()) } returns counterBuilder
        every { counterBuilder.register(registry) } returns counter
    }

    @After
    fun teardown() {
        unmockkStatic(Counter::class)
    }

    @Test
    fun `increments api call metric successfully`() {
        metricsGenerator.incrementApiCall()

        verify {
            Counter.builder("file_processing_calls_total")
            counterBuilder.description("Total number of file processing API calls")
            counterBuilder.register(registry)
            counter.increment()
        }
    }

    @Test
    fun `increments institution metric with valid institution`() {
        val institution = "TestInstitution"

        metricsGenerator.incrementInstitution(institution)

        verify {
            Counter.builder("file_processing_requests_by_institution_total")
            counterBuilder.description("Total number of file processing requests by institution")
            counterBuilder.tag("institution", institution)
            counterBuilder.register(registry)
            counter.increment()
        }
    }

    @Test
    fun `increments error metric for http exception`() {
        val exception = mockk<HttpException>()
        every { exception.status } returns HttpStatusCode.BadRequest

        metricsGenerator.incrementError(exception)

        verify {
            Counter.builder("file_processing_errors_total")
            counterBuilder.description("Total number of errors during file processing")
            counterBuilder.tag("error_type", "Bad Request")
            counterBuilder.tag("error_code", "400")
            counterBuilder.register(registry)
            counter.increment()
        }
    }

    @Test
    fun `increments error metric for file processing exception`() {
        val exception = mockk<FileProcessingException>()
        every { exception.status } returns HttpStatusCode.InternalServerError

        metricsGenerator.incrementError(exception)

        verify {
            Counter.builder("file_processing_errors_total")
            counterBuilder.description("Total number of errors during file processing")
            counterBuilder.tag("error_type", "Internal Server Error")
            counterBuilder.tag("error_code", "500")
            counterBuilder.register(registry)
            counter.increment()
        }
    }

    @Test
    fun `increments error metric for generic exception`() {
        val exception = Exception("Generic error")

        metricsGenerator.incrementError(exception)

        verify {
            Counter.builder("file_processing_errors_total")
            counterBuilder.description("Total number of errors during file processing")
            counterBuilder.tag("error_type", "Internal Server Error")
            counterBuilder.tag("error_code", "500")
            counterBuilder.register(registry)
            counter.increment()
        }
    }
}