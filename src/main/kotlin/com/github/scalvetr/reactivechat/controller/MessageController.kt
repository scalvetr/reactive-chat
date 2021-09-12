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
        const val SEND_STREAM = "stream"
        const val RECEIVE_STREAM = "stream"
        const val CHANNEL = "channel"
    }

    /**
     * Bi-Directional stream: rsocket client => requestChannel
     */
    /* https://stremler.io/2020-05-31-rsocket-messaging-with-spring-boot-and-rsocket-js/ */
    @MessageMapping(CHANNEL)
    fun channel(@Payload inboundMessages: Flow<Message>) = messageService.channel(inboundMessages)


    /**
     * Inbound stream: rsocket client => requestResponse, fireAndForget, requestChannel (only sending)
     */
    @MessageMapping(RECEIVE_STREAM)
    suspend fun receive(@Payload inboundMessages: Flow<Message>) =
        messageService.post(inboundMessages)

    /**
     * Outbound stream: rsocket client => requestStream
     */
    @MessageMapping(SEND_STREAM)
    fun send(): Flow<Message> = messageService
        .stream()
        .onStart {
            emitAll(messageService.latest())
        }

}
