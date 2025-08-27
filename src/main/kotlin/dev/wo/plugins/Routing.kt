package dev.wo.plugins

import dev.wo.application.web.routes.fileRouting
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/health-check") {
            call.respondText("Up and running!")
        }

        get("/demo") {
            call.respondRedirect("/demo/index.html")
        }

        static("/demo") {
            resources("static")
        }

        fileRouting()
    }
}
