package dev.wo.config

data class AppConfiguration(
    val app: App,
    val rateLimiter: RateLimiterConfig
)

data class App(
    val profile: String
)

data class RateLimiterConfig(
    val requests: Int,
    val duration: String
)
