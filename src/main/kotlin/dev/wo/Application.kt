package dev.wo

import dev.wo.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDependencyInjection()
    configureMetrics()
    configureSerialization()
    configureHTTP()
    configureRouting()
    configureCors()
}
