package com.github.scalvetr.reactivechat.service

import com.github.scalvetr.reactivechat.service.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageService {

    fun latest(): Flow<Message>

    fun after(messageId: String): Flow<Message>

    fun stream(): Flow<Message>

    suspend fun post(messages: Flow<Message>)
}
