package com.github.scalvetr.reactivechat.service

import com.github.scalvetr.reactivechat.service.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageService {

    suspend fun latest(): Flow<Message>

    suspend fun after(messageId: String): Flow<Message>

    fun stream(): Flow<Message>

    suspend fun post(messages: Flow<Message>)

    suspend fun post(messages: Message) : Message

}
