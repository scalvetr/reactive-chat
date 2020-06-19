package com.github.scalvet.reactivechat.integration.handler

import com.github.scalvet.reactivechat.config.loggerFor
import com.github.scalvet.reactivechat.domain.model.ChatMessage
import com.github.scalvet.reactivechat.integration.messaging.ChatMessageSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.util.function.Consumer


@Component
class InMessageHandler @Autowired constructor(val producer: ChatMessageSender) :
        Consumer<ChatMessage> {
    private val log = loggerFor(javaClass)

    override fun accept(message: ChatMessage) {
        log.debug("in message {}", message)
        producer.send(message.recipient, message)
    }

}