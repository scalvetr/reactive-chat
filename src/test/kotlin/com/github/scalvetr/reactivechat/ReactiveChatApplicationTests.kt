package com.github.scalvetr.reactivechat

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "spring.liquibase.enabled=false"
    ]
)
class ReactiveChatApplicationTests {

    @Test
    fun contextLoads() {
    }

}