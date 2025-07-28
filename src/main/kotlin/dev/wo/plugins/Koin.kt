package dev.wo.plugins

import dev.wo.infrastructure.metrics.MetricsGenerator
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.koin.dsl.module

val appModule = module {
    single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
    single { MetricsGenerator(get()) }
}
