package com.github.scalvet.reactivechat.integration.rest

import com.github.scalvet.reactivechat.config.loggerFor
import com.github.scalvet.reactivechat.domain.model.ChatMessage
import com.github.scalvet.reactivechat.integration.handler.InMessageHandler
import com.github.scalvet.reactivechat.integration.handler.OutMessageHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/v1/messages")
class MessagesController @Autowired constructor(val inMsg: InMessageHandler, val outMsg: OutMessageHandler) {
    private val log = loggerFor(javaClass)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun send(authentication: Authentication, messages: Flux<ChatMessage>): ResponseEntity<Unit> {
        messages
                // make sure origin matches userName
                .map { m -> ChatMessage(recipient = m.recipient, message = m.message, origin = authentication.name) }
                .subscribe(inMsg)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun receive(authentication: Authentication): Flux<ChatMessage> {
        return Flux.from(MessagesPublisher(outMsg.subscribe(authentication.name)))
    }

}