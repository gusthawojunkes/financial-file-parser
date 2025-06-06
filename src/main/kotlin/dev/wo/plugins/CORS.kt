package dev.wo.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("Institution")
        allowHeader("File-Type")
        allowHeader("CSV-Separator")
        allowHeader("DateTime-Pattern")
        allowHeader("Invoice-Type")
        anyHost()
    }
}