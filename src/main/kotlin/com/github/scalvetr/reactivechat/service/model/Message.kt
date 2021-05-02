package com.github.scalvetr.reactivechat.service.model

import java.time.Instant

data class Message(val content: String, val sender: User, val sent: Instant, val id: Long? = null)
