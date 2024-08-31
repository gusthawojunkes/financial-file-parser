package dev.wo.plugins

import dev.wo.application.web.routes.fileRouting
import dev.wo.infrastructure.adapters.messaging.RabbitMQConfig
import dev.wo.infrastructure.adapters.messaging.RabbitMQListener
import dev.wo.infrastructure.adapters.messaging.RabbitMQSender
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

val channel = RabbitMQConfig.channel
val sender = RabbitMQSender(channel)
val listener = RabbitMQListener(channel)

fun Application.configureRouting() {
    routing {
        get("/health-check") {
            call.respondText("Up and running!")
        }

        post("/send") {
            val message = call.receiveText()
            sender.send(message)
            call.respondText("Message sent: $message")
        }

        fileRouting()
    }
    listener.startListening()
}
