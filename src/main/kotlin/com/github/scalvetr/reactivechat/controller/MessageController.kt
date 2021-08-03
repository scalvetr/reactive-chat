package com.github.scalvetr.reactivechat.controller

import com.github.scalvetr.reactivechat.service.MessageService
import com.github.scalvetr.reactivechat.service.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("api.v1.messages")
class MessageController(val messageService: MessageService) {

    @MessageMapping("stream")
    suspend fun receive(@Payload inboundMessages: Flow<Message>) =
        messageService.post(inboundMessages)

    @MessageMapping("stream")
    fun send(): Flow<Message> = messageService
        .stream()
        .onStart {
            emitAll(messageService.latest())
        }

}
