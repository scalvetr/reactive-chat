package com.github.scalvetr.reactivechat.common

import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

open class PostgreSqlContainer {

    companion object {

        val container: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:14.1-alpine").apply {
            withDatabaseName("it-db")
            withUsername("it-db-user")
            withPassword("it-db-password")
            withReuse(true)
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerPgProperties(registry: DynamicPropertyRegistry) {
            registry.add("POSTGRES_HOST") { container.host }
            registry.add("POSTGRES_PORT") { container.firstMappedPort }
            registry.add("POSTGRES_DB") { container.databaseName }
            registry.add("POSTGRES_USER") { container.username }
            registry.add("POSTGRES_PASSWORD") { container.password }
            // By default, SQL database initialization is only performed when using an embedded in-memory database.
            // https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#howto.data-initialization.using-basic-sql-scripts
            registry.add("spring.sql.init.mode") { "always" }
        }

        @JvmStatic
        @BeforeAll
        fun startContainer() {
            container.start()
        }
    }
}
