package dev.wo.plugins

import dev.wo.infrastructure.serialization.adapters.LocalDateTimeAdapter
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import java.time.LocalDateTime

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            setPrettyPrinting()
            serializeNulls()
        }
    }
}
