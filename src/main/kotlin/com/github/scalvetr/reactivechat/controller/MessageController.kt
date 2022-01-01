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
    companion object {
        const val STREAM = "stream"
    }

    /**
     * Inbound stream: rsocket client => requestChannel (only sending)
     */
    //@MessageMapping(STREAM)
    //suspend fun receive(@Payload inboundMessages: Flow<Message>) =
    //   messageService.post(inboundMessages)

    /**
     * Inbound stream: rsocket client => requestResponse, fireAndForget (only sending)
     */
    @MessageMapping(STREAM)
    suspend fun receive(@Payload message: Message): Message =
        messageService.post(message)

    /**
     * Outbound stream: rsocket client => requestStream
     */
    @MessageMapping(STREAM)
    suspend fun send(): Flow<Message> = messageService
        .stream()
        .onStart {
            emitAll(messageService.latest())
        }
}
