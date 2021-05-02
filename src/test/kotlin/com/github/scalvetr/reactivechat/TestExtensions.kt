package com.github.scalvetr.reactivechat

import com.github.scalvetr.reactivechat.repository.model.MessageEntity
import com.github.scalvetr.reactivechat.service.model.Message
import java.time.temporal.ChronoUnit.MILLIS

fun MessageEntity.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))

fun Message.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))
