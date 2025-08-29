package dev.wo.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection(config: ApplicationConfig) {
    install(Koin) {
        slf4jLogger()
        modules(appModule(config))
    }
}