package com.github.scalvetr.reactivechat

import app.cash.turbine.test
import com.github.scalvetr.reactivechat.common.PostgreSqlContainer
import com.github.scalvetr.reactivechat.controller.MessageController
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
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class MessageControllerITest(
    @Autowired val rsocketBuilder: RSocketRequester.Builder,
    @Autowired val messageRepository: MessageRepository,
    @LocalServerPort val serverPort: Int
) : PostgreSqlContainer() {
    var lastMessageId: Long? = null
    val now: Instant = Instant.now().truncatedTo(MILLIS)

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
                        ContentType.PLAINTEXT,
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
                .route("api.v1.messages.${MessageController.SEND_STREAM}")
                .retrieveFlow<Message>()
                .test {
                    expectThat(expectItem().prepareForTesting())
                        .isEqualTo(
                            Message(
                                User("test", URL("http://test.com")),
                                "*testMessage*",
                                now.minusSeconds(2)
                            )
                        )

                    expectThat(expectItem().prepareForTesting())
                        .isEqualTo(
                            Message(
                                User("test1", URL("http://test.com")),
                                "<body><p><strong>testMessage2</strong></p></body>",
                                now.minusSeconds(1)
                            )
                        )
                    expectThat(expectItem().prepareForTesting())
                        .isEqualTo(
                            Message(
                                User("test2", URL("http://test.com")),
                                "<body><p><code>testMessage3</code></p></body>",
                                now
                            )
                        )
                    expectNoEvents()

                    launch {
                        rSocketRequester.route("api.v1.messages.${MessageController.RECEIVE_STREAM}")
                            .dataWithType(flow {
                                emit(
                                    Message(
                                        User("test", URL("http://test.com")),
                                        "`HelloWorld`",
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
                                User("test", URL("http://test.com")),
                                "<body><p><code>HelloWorld</code></p></body>",
                                now.plusSeconds(1)
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

                rSocketRequester.route("api.v1.messages.${MessageController.RECEIVE_STREAM}")
                    .dataWithType(flow {
                        emit(
                            Message(
                                User("test", URL("http://test.com")),
                                "`HelloWorld`",
                                now.plusSeconds(1)
                            )
                        )
                    })
                    .retrieveFlow<Void>()
                    .collect()
            }

            delay(Duration.seconds(2))

            messageRepository.findAll()
                .first { it.content.contains("HelloWorld") }
                .apply {
                    expectThat(this.prepareForTesting()) {
                        get { content } isEqualTo "`HelloWorld`"
                        get { contentType } isEqualTo ContentType.MARKDOWN
                        get { sent } isEqualTo now.plusSeconds(1)
                        get { username } isEqualTo "test"
                        get { userAvatarImageLink } isEqualTo "http://test.com"
                    }
                }
        }
    }
}
