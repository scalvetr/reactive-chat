package com.github.scalvet.reactivechat.integration.rest

import com.github.scalvet.reactivechat.config.loggerFor
import com.github.scalvet.reactivechat.domain.model.ChatMessage
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import java.util.stream.Stream

class MessagesPublisher constructor(private val stream: Stream<ChatMessage>) : Publisher<ChatMessage> {
    private val log = loggerFor(javaClass)

    override fun subscribe(subscriber: Subscriber<in ChatMessage>) {
        try {
            stream.forEach(subscriber::onNext)
            subscriber.onComplete()
        } catch (e: Throwable) {
            subscriber.onError(e)
        }
    }
}