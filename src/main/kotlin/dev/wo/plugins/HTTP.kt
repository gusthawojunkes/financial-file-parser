package dev.wo.plugins

import dev.wo.config.AppConfiguration
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.seconds

fun Application.configureHTTP() {
    val config by inject<AppConfiguration>()
    val rateLimiterConfig = config.rateLimiter

    routing {
        swaggerUI(path = "swagger")
    }

    install(RateLimit) {
        global {
            rateLimiter(
                limit = rateLimiterConfig.limitPerPeriod,
                refillPeriod = rateLimiterConfig.refillPeriodInSeconds.seconds
            )
        }
    }

    install(StatusPages) {
        status(HttpStatusCode.TooManyRequests) { call, status ->
            val retryAfter = call.response.headers["Retry-After"]
            call.respondText(text = "429: Too many requests. Wait for $retryAfter seconds.", status = status)
        }
    }
}
