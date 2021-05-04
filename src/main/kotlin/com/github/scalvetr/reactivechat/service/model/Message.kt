package com.github.scalvetr.reactivechat.service.model

import java.time.Instant

data class Message(val sender: User, val content: String, val timestamp: Instant, val id: Long? = null)
