import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.4.30"
	kotlin("plugin.spring") version "1.4.30"
}

group = "com.github.scalvetr"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven("https://dl.bintray.com/jetbrains/markdown")
}

extra["springCloudVersion"] = "2020.0.1"

dependencies {
	implementation(project(":webapp"))

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-rsocket")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.github.javafaker:javafaker:1.0.2")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("org.jetbrains:markdown:0.1.45")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("app.cash.turbine:turbine:0.3.0") // testing library for kotlin coroutines

	// r2dbc driver
	runtimeOnly("io.r2dbc:r2dbc-postgresql")

	// liquibase jdbc
	implementation("org.liquibase:liquibase-core")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.springframework:spring-jdbc")


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
		jvmTarget = "11"
	}
}
