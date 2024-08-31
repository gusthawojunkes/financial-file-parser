package dev.wo.infrastructure.adapters.messaging

import com.rabbitmq.client.Channel

class RabbitMQSender(private val channel: Channel) {
    fun send(message: String) {
        channel.basicPublish("", "q.file-processing", null, message.toByteArray())
    }
}