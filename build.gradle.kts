import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "com.github.scalvetr"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2021.0.0"

dependencies {
	implementation(project(":webapp"))

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-rsocket")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("org.jetbrains:markdown:0.2.4")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.strikt:strikt-core:0.33.0")

	testImplementation("app.cash.turbine:turbine:0.7.0") // testing library for kotlin coroutines
	testImplementation("org.testcontainers:postgresql:1.15.3")
	testImplementation("io.r2dbc:r2dbc-postgresql")

	// r2dbc driver
	//runtimeOnly("io.r2dbc:r2dbc-postgresql")
	runtimeOnly("io.r2dbc:r2dbc-h2")

	// liquibase jdbc
	implementation("org.liquibase:liquibase-core")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.postgresql:postgresql")


	// TODO switch to MongoDB
	//implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

	// no security
	//implementation("org.springframework.boot:spring-boot-starter-security")
	//implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	//testImplementation("org.springframework.security:spring-security-test")

	// no cloud stream
	//implementation("org.springframework.cloud:spring-cloud-stream")
	//implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
	//testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<BootJar> {
	this.archiveFileName.set("reactive-chat.jar")
}
