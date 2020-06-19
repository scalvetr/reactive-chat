package com.github.scalvet.reactivechat.integration.handler

import com.github.scalvet.reactivechat.config.loggerFor
import com.github.scalvet.reactivechat.domain.model.ChatMessage
import org.springframework.stereotype.Component
import java.util.function.Supplier
import java.util.stream.Stream


@Component
class OutMessageHandler {
    private val log = loggerFor(javaClass)

    fun send(message: ChatMessage) {
        TODO("not yet implemented")
    }

    fun subscribe(recipient: String): Stream<ChatMessage> {
        TODO("not yet implemented")
    }



}