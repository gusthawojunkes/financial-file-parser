package dev.wo

import dev.wo.infrastructure.adapters.messaging.RabbitMQConfig
import dev.wo.infrastructure.adapters.messaging.RabbitMQListener
import dev.wo.infrastructure.adapters.messaging.RabbitMQSender
import dev.wo.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureRouting()
}
