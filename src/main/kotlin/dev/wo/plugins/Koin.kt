package dev.wo.plugins

import dev.wo.config.App
import dev.wo.config.AppConfiguration
import dev.wo.config.RateLimiterConfig
import dev.wo.infrastructure.metrics.MetricsGenerator
import io.ktor.server.config.ApplicationConfig
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.koin.dsl.module

fun appModule(config: ApplicationConfig) = module {
    single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
    single { MetricsGenerator(get()) }
    single<AppConfiguration> {
        AppConfiguration(
            app = App(
                profile = config.property("app.profile").getString()
            ),
            rateLimiter = RateLimiterConfig(
                requests = config.property("rate-limiter.requests").getString().toInt(),
                duration = config.property("rate-limiter.duration").getString()
            )
        )
    }
}
