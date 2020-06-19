package com.github.scalvet.reactivechat.domain.model;

data class ChatMessage(val recipient: String, val origin: String, val message: String)