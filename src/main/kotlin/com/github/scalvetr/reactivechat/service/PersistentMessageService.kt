package com.github.scalvetr.reactivechat.service

import com.github.scalvetr.reactivechat.loggerFor
import com.github.scalvetr.reactivechat.repository.MessageRepository
import com.github.scalvetr.reactivechat.service.model.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    val log = loggerFor(javaClass)

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

    override fun channel(messages: Flow<Message>): Flow<Message> {
        // TODO fix: the returning flow only streams the messages received by the input messages flow. Not all the messages emitted to sender.
        return messages
            .onEach { sender.emit(it.asRendered()) }
            .map { it.asDomainObject() }
            .let { messageRepository.saveAll(it) }
            .map { it.asViewModel() }
    }

    /*
    override fun channel(messages: Flow<Message>): Flow<Message> {
        runBlocking {
            messages
                .onEach { sender.emit(it.asRendered()) }
                .map { it.asDomainObject() }
                .let { messageRepository.saveAll(it) }
        }
        return flow {
            sender.onEach { emit(it) }
        }
    }
    */
    /*
    override fun channel(messages: Flow<Message>): Flow<Message> = flow {
        coroutineScope {
            log.debug("PersistentMessageService#channel")
            launch {
                log.debug("PersistentMessageService#channel->send")
                messageRepository.findLatest()
                    .onEach { emit(it.asViewModel()) } // emit all messages streamed from the database.
            }
            log.debug("PersistentMessageService#channel->listen")
            messages
                .map { it.asDomainObject() } // map to domain object
                .let { messageRepository.saveAll(it) } // send to repository
                .collect()
        }
    }*/
    /*
    override fun channel(messages: Flow<Message>): Flow<Message> = flow {
        coroutineScope {
            messages
                .onStart { sender.onEach { emit(it) } }
                .onEach { sender.emit(it.asRendered()) }
                .map { it.asDomainObject() }
                .let { messageRepository.saveAll(it) }.collect()
        }
    }
     */
}
