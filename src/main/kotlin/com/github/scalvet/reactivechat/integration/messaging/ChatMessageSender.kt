package com.github.scalvet.reactivechat.integration.messaging

import com.github.scalvet.reactivechat.config.loggerFor
import com.github.scalvet.reactivechat.domain.model.ChatMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ChatMessageSender
@Autowired constructor(
        val kafkaTemplate: KafkaTemplate<String, ChatMessage>,
        @Value("\${application.kafka.topic}") val topic: String
) {
    private val log = loggerFor(javaClass)


    fun send(recipient: String, data: ChatMessage) {
        log.info("sending data='{}' to recipient='{}'", data, recipient)
        kafkaTemplate.send(topic, recipient, data)
    }

}