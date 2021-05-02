package com.github.scalvetr.reactivechat.service

import com.github.scalvetr.reactivechat.repository.model.ContentType
import com.github.scalvetr.reactivechat.repository.model.MessageEntity
import com.github.scalvetr.reactivechat.service.model.Message
import com.github.scalvetr.reactivechat.service.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.URL
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser



fun Message.asDomainObject(contentType: ContentType = ContentType.MARKDOWN): MessageEntity = MessageEntity(
    id,
    content,
    contentType,
    sent,
    sender.name,
    sender.avatarImageLink.toString()
)

fun MessageEntity.asViewModel(): Message = Message(
    contentType.render(content),
    User(username, URL(userAvatarImageLink)),
    sent,
    id
)
fun Message.asRendered(contentType: ContentType = ContentType.MARKDOWN): Message =
    this.copy(content = contentType.render(this.content))

fun Flow<MessageEntity>.mapToViewModel(): Flow<Message> = map { it.asViewModel() }

fun List<MessageEntity>.mapToViewModel(): List<Message> = map { it.asViewModel() }

fun ContentType.render(content: String): String = when (this) {
    ContentType.PLAIN -> content
    ContentType.MARKDOWN -> {
        val flavour = CommonMarkFlavourDescriptor()
        HtmlGenerator(content, MarkdownParser(flavour).buildMarkdownTreeFromString(content), flavour).generateHtml()
    }
}
