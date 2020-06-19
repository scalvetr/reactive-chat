package com.github.scalvet.reactivechat.integration.messaging

import com.github.scalvet.reactivechat.config.loggerFor
import com.github.scalvet.reactivechat.domain.model.ChatMessage
import com.github.scalvet.reactivechat.integration.handler.OutMessageHandler
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.core.AbstractMessageSendingTemplate
import org.springframework.messaging.core.MessageSendingOperations
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*

@Component
class ChatMessageReceiver @Autowired constructor(val outMessageHandler: OutMessageHandler){
    private val log = loggerFor(javaClass)

    @KafkaListener(topics = ["\${application.kafka.topic}"])
    fun receive(consumerRecord: ConsumerRecord<String, ChatMessage>) {
        log.info("received data='{}'", consumerRecord.toString())
        outMessageHandler.send(consumerRecord.value())
    }

}