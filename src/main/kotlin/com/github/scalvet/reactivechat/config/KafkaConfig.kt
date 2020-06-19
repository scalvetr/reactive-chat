package com.github.scalvet.reactivechat.config

import com.github.scalvet.reactivechat.domain.model.ChatMessage
import com.github.scalvet.reactivechat.integration.handler.OutMessageHandler
import org.apache.kafka.streams.kstream.KStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer


@Configuration
class KafkaConfig @Autowired constructor(var outMessageConsumer: OutMessageHandler){


    //Basic Consumer
    @Bean
    fun consume(): Consumer<KStream<String, ChatMessage>> {
        return Consumer {
            input: KStream<String, ChatMessage> ->
        }
    }

}