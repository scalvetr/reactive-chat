package com.github.scalvetr.reactivechat.common

import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

open class PostgreSqlContainer {

    companion object {

        val container: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:12.4-alpine").apply {
            withDatabaseName("it-db")
            withUsername("it-db-user")
            withPassword("it-db-password")
            withReuse(true)
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerPgProperties(registry: DynamicPropertyRegistry) {
            registry.add("postgres_host") { container.host }
            registry.add("postgres_port") { container.firstMappedPort }
            registry.add("postgres_db") { container.databaseName }
            registry.add("postgres_user") { container.username }
            registry.add("postgres_password") { container.password }
        }

        @JvmStatic
        @BeforeAll
        fun startContainer() {
            container.start()
        }
    }
}
