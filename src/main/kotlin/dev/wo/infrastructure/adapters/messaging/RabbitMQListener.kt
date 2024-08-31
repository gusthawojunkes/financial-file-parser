package dev.wo.infrastructure.adapters.messaging

import com.rabbitmq.client.Channel
import com.rabbitmq.client.DeliverCallback

class RabbitMQListener(private val channel: Channel) {
    fun startListening() {
        val deliverCallback = DeliverCallback { _, delivery ->
            val message = String(delivery.body, Charsets.UTF_8)
            println("Received message: $message")
        }
        channel.basicConsume("q.file-processing", true, deliverCallback, { _ -> })
    }
}