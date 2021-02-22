package com.github.scalvetr.reactivechat.repository.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("MESSAGES")
data class MessageEntity(
    val content: String,
    val contentType: ContentTypeEnum,
    val timestamp: Instant,
    val username: String,
    val userAvatarImageLink: String,
    @Id var id: String? = null
)