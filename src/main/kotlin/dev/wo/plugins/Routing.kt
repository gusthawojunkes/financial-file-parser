package dev.wo.plugins

import dev.wo.application.web.routes.processFileRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        processFileRoutes()
    }
}
