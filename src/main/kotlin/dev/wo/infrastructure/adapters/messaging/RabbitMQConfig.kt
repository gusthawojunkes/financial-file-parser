package dev.wo.infrastructure.adapters.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import java.util.concurrent.TimeUnit

object RabbitMQConfig {
    private const val QUEUE_NAME = "q.file-processing"
    private const val MAX_RETRIES = 5
    private const val RETRY_DELAY = 5L // seconds

    private val factory = ConnectionFactory().apply {
        host = System.getenv("RABBITMQ_HOST") ?: "localhost"
        port = (System.getenv("RABBITMQ_PORT") ?: "5672").toInt()
        username = System.getenv("RABBITMQ_DEFAULT_USER") ?: "guest"
        password = System.getenv("RABBITMQ_DEFAULT_PASS") ?: "guest"
    }

    val connection: Connection = createConnectionWithRetry()
    val channel: Channel = connection.createChannel().apply {
        queueDeclare(QUEUE_NAME, true, false, false, null)
    }

    private fun createConnectionWithRetry(): Connection {
        var retries = 0
        while (retries < MAX_RETRIES) {
            try {
                return factory.newConnection()
            } catch (e: Exception) {
                retries++
                println("Failed to connect to RabbitMQ, retrying in $RETRY_DELAY seconds... ($retries/$MAX_RETRIES)")
                TimeUnit.SECONDS.sleep(RETRY_DELAY)
            }
        }
        throw RuntimeException("Failed to connect to RabbitMQ after $MAX_RETRIES attempts")
    }
}