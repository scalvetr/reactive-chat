package com.github.scalvetr.reactivechat.service

import com.github.scalvetr.reactivechat.repository.MessageRepository
import com.github.scalvetr.reactivechat.service.model.Message
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service

@Service
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    val sender: MutableSharedFlow<Message> = MutableSharedFlow()

    override fun latest(): Flow<Message> =
        messageRepository.findLatest()
            .mapToViewModel()

    override fun after(messageId: String): Flow<Message> =
        messageRepository.findLatest(messageId)
            .mapToViewModel()

    override fun stream(): Flow<Message> = sender

    override suspend fun post(messages: Flow<Message>) =
        messages
            .onEach { sender.emit(it.asRendered()) }
            .map { it.asDomainObject() }
            .let { messageRepository.saveAll(it) }
            .collect()
}