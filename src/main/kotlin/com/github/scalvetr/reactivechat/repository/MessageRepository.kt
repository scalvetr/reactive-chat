package com.github.scalvetr.reactivechat.repository

import com.github.scalvetr.reactivechat.repository.model.MessageEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface MessageRepository : CoroutineCrudRepository<MessageEntity, String> {

    // language=SQL
    @Query("""
        SELECT * FROM messages
        ORDER BY sent ASC
    """)
    fun findLatest(): Flow<MessageEntity>

    // language=SQL
    @Query("""
        SELECT * FROM messages
        WHERE sent > (SELECT sent FROM messages WHERE id = :id)
        ORDER BY sent ASC 
    """)
    fun findLatest(@Param("id") id: String): Flow<MessageEntity>
}
