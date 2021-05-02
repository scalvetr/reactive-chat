package com.github.scalvetr.reactivechat

import app.cash.turbine.test
import com.github.scalvetr.reactivechat.common.PostgreSqlContainer
import com.github.scalvetr.reactivechat.repository.MessageRepository
import com.github.scalvetr.reactivechat.repository.model.ContentType
import com.github.scalvetr.reactivechat.repository.model.MessageEntity
import com.github.scalvetr.reactivechat.service.model.Message
import com.github.scalvetr.reactivechat.service.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.dataWithType
import org.springframework.messaging.rsocket.retrieveFlow
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class MessageControllerITest(
    @Autowired val rsocketBuilder: RSocketRequester.Builder,
    @Autowired val messageRepository: MessageRepository,
    @LocalServerPort val serverPort: Int
) : PostgreSqlContainer() {
    var lastMessageId: Long? = null
    val now: Instant = Instant.now()

    @BeforeEach
    fun setUp() {
        runBlocking {
            val secondBeforeNow = now.minusSeconds(1)
            val twoSecondBeforeNow = now.minusSeconds(2)
            val savedMessages = messageRepository.saveAll(
                listOf(
                    MessageEntity(
                        null,
                        "*testMessage*",
                        ContentType.PLAIN,
                        twoSecondBeforeNow,
                        "test",
                        "http://test.com"
                    ),
                    MessageEntity(
                        null,
                        "**testMessage2**",
                        ContentType.MARKDOWN,
                        secondBeforeNow,
                        "test1",
                        "http://test.com"
                    ),
                    MessageEntity(
                        null,
                        "`testMessage3`",
                        ContentType.MARKDOWN,
                        now,
                        "test2",
                        "http://test.com"
                    )
                )
            ).toList()
            lastMessageId = savedMessages.first().id
        }
    }

    @AfterEach
    fun tearDown() {
        runBlocking {
            messageRepository.deleteAll()
        }
    }

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @Test
    fun `test that messages API streams latest messages`() {
        runBlocking {
            val rSocketRequester =
                rsocketBuilder.websocket(URI("ws://localhost:${serverPort}/rsocket"))

            rSocketRequester
                .route("api.v1.messages.stream")
                .retrieveFlow<Message>()
                .test {
                    expectThat(expectItem().prepareForTesting())
                        .isEqualTo(
                            Message(
                                "*testMessage*",
                                User("test", URL("http://test.com")),
                                now.minusSeconds(2).truncatedTo(MILLIS)
                            )
                        )

                    expectThat(expectItem().prepareForTesting())
                        .isEqualTo(
                            Message(
                                "<body><p><strong>testMessage2</strong></p></body>",
                                User("test1", URL("http://test.com")),
                                now.minusSeconds(1).truncatedTo(MILLIS)
                            )
                        )
                    expectThat(expectItem().prepareForTesting())
                        .isEqualTo(
                            Message(
                                "<body><p><code>testMessage3</code></p></body>",
                                User("test2", URL("http://test.com")),
                                now.truncatedTo(MILLIS)
                            )
                        )
                    expectNoEvents()

                    launch {
                        rSocketRequester.route("api.v1.messages.stream")
                            .dataWithType(flow {
                                emit(
                                    Message(
                                        "`HelloWorld`",
                                        User("test", URL("http://test.com")),
                                        now.plusSeconds(1)
                                    )
                                )
                            })
                            .retrieveFlow<Void>()
                            .collect()
                    }

                    expectThat(expectItem().prepareForTesting())
                        .isEqualTo(
                            Message(
                                "<body><p><code>HelloWorld</code></p></body>",
                                User("test", URL("http://test.com")),
                                now.plusSeconds(1).truncatedTo(MILLIS)
                            )
                        )

                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @ExperimentalTime
    @Test
    fun `test that messages streamed to the API is stored`() {
        runBlocking {
            launch {
                val rSocketRequester =
                    rsocketBuilder.websocket(URI("ws://localhost:${serverPort}/rsocket"))

                rSocketRequester.route("api.v1.messages.stream")
                    .dataWithType(flow {
                        emit(
                            Message(
                                "`HelloWorld`",
                                User("test", URL("http://test.com")),
                                now.plusSeconds(1)
                            )
                        )
                    })
                    .retrieveFlow<Void>()
                    .collect()
            }

            delay(2.seconds)

            messageRepository.findAll()
                .first { it.content.contains("HelloWorld") }
                .apply {
                    expectThat(this.prepareForTesting()) {
                        get { content } isEqualTo "`HelloWorld`"
                        get { contentType } isEqualTo ContentType.MARKDOWN
                        get { sent } isEqualTo now.plusSeconds(1).truncatedTo(MILLIS)
                        get { username } isEqualTo "test"
                        get { userAvatarImageLink } isEqualTo "http://test.com"
                    }
                }
        }
    }
}
