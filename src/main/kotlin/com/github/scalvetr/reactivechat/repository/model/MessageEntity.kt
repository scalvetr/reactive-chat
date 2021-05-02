package com.github.scalvetr.reactivechat.repository.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("MESSAGES")
data class MessageEntity(
    @Id
    var id: Long? = null,
    val content: String,
    val contentType: ContentType,
    val sent: Instant,
    val username: String,
    val userAvatarImageLink: String
)
