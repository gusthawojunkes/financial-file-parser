package dev.wo.application.web.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.processFileRoutes() {
    route("api/v1") {
        route("file") {
            post("process") {
                val file = call.receiveMultipart()

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}