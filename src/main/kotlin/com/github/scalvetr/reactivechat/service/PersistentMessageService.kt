package com.github.scalvetr.reactivechat.service

import com.github.scalvetr.reactivechat.repository.MessageRepository
import com.github.scalvetr.reactivechat.service.model.Message
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service

@Service
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    val stream: MutableSharedFlow<Message> = MutableSharedFlow()

    override suspend fun latest(): Flow<Message> =
        messageRepository.findLatest()
            .mapToViewModel()

    override suspend fun after(messageId: String): Flow<Message> =
        messageRepository.findLatest(messageId)
            .mapToViewModel()

    override fun stream(): Flow<Message> = stream


    override suspend fun post(messages: Flow<Message>) =
        messages
            .onEach { stream.emit(it.asRendered()) }
            .map { it.asDomainObject() }
            .let { messageRepository.saveAll(it) }
            .collect()

    override suspend fun post(message: Message) {
        stream.emit(message)
        messageRepository.save(message.asDomainObject())
    }

}
