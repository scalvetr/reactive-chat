package com.github.scalvetr.reactivechat.service.model

import java.time.Instant

data class Message(val content: String, val sender: User, val timestamp: Instant, val id: String? = null)