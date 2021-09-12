# Reactive Chat

## :computer: Build project

### Prerequisites
Node + Angular CLI
```shell
brew install node
npm install -g npm@7.20.3
npm install -g @angular/cli@12.1.4
npm install -g typescript@4.3.5
```

### Project
Build & test
```shell
./gradlew clean build
```
Package jar & build docker image
```shell
./gradlew bootJar
# build image
docker build . --build-arg JAR_FILE=build/libs/reactive-chat.jar -t reactive-chat

```

### UI only
Angular SPA
```shell
# build
cd webapp
ng build

# test
ng serve
```

## :running_man: Run
Gradle 
```shell
docker-compose up -d postgres
#./mvnw spring-boot:run
./gradlew bootRun

curl http://localhost:8080/actuator/health
```

Docker Compose
```shell
docker-compose up

curl http://localhost:8080/actuator/health
```

Listen all RSocket messages

```shell
brew install yschimke/tap/rsocket-cli
rsocket-cli --route=api.v1.messages.stream  ws://localhost:8080/rsocket
```

## :jigsaw: Naming and modules

**Build system:** gradle


Modules:
* Backend (reactive-chat)
* Frontend (Webapp)


Reactive chat includes the webapp module. Config:
#### **`settings.gradle.kts`**
The module is build.
```kotlin
include("webapp")
```

#### **`build.gradle.kts`**
The module is included as a library `webapp.jar` in the generated Spring Boot application.
```kotlin
	implementation(project(":webapp"))
```

#### Build webapp node application

The following plugin is used to build the angular application`com.github.node-gradle.node`.
#### **`build.gradle.kts`**
```kotlin
plugins {
    java
    id("com.github.node-gradle.node") version "3.1.0"
}
```

Plugin setup
```kotlin
val buildTask = tasks.register<NpxTask>("buildWebapp") {
    command.set("ng")
    args.set(listOf("build", "--prod"))
    dependsOn(tasks.npmInstall, lintTask)
    inputs.dir(project.fileTree("src").exclude("**/*.spec.ts"))
    inputs.dir("node_modules")
    inputs.files("angular.json", ".browserslistrc", "tsconfig.json", "tsconfig.app.json")
    outputs.dir("${project.buildDir}/webapp")
}


sourceSets {
    java {
        main {
            resources {
                // This makes the processResources task automatically depend on the buildWebapp one
                srcDir(buildTask)
            }
        }
    }
}
```


#### Webapp structure

```bash
unzip webapp.jar 
> Archive:  webapp.jar
>    creating: META-INF/
>   inflating: META-INF/MANIFEST.MF    
>    creating: static/
>   inflating: static/polyfills.3e143dd5565bcc9a954f.js  
>   inflating: static/favicon.ico      
>   inflating: static/index.html       
>   inflating: static/3rdpartylicenses.txt  
>   inflating: static/styles.31d6cfe0d16ae931b73c.css  
>   inflating: static/runtime.2ea037e6cf819e27a11c.js  
>   inflating: static/main.6abaf37c83845abbf9e4.js  
>    creating: static/assets/
>    creating: static/assets/images/
>   inflating: static/assets/images/user-profile.png
```

#### **`angular.json`**
```json
  "projects": {
    "chat": {
      "projectType": "application",
      "root": "",
      "sourceRoot": "src",
      "prefix": "app",
      "architect": {
        "build": {
          "builder": "@angular-devkit/build-angular:browser",
          "options": {
            "outputPath": "build/webapp/static",
```





## :memo: Useful links

**Gradle Node Plugin**

* https://github.com/node-gradle/gradle-node-plugin/blob/3.1.0/README.md

**RSocket**

* https://spring.io/guides/tutorials/spring-webflux-kotlin-rsocket/ -> [Github](https://github.com/spring-guides/tut-spring-webflux-kotlin-rsocket)
* https://medium.com/swlh/building-a-chat-application-with-angular-and-spring-rsocket-3cd8013f2f55  -> [Github](https://github.com/hantsy/angular-spring-rsocket-sample)

**Spring Boot + Kafka Reactive**

* https://medium.com/swlh/angular-spring-boot-kafka-how-to-stream-realtime-data-the-reactive-way-510a0f1e5881 -> [Github - client](https://github.com/davemaier/reactivekafkaclient) [Github - server](https://github.com/davemaier/reactivekafkaserver)
* https://dzone.com/articles/kafka-with-spring-cloud-stream
* https://github.com/reactor/reactor-kafka/
* https://cloud.spring.io/spring-cloud-static/spring-cloud-stream-binder-kafka/3.0.4.RELEASE/reference/html/spring-cloud-stream-binder-kafka.html#_configuration_options

**Spring & Web Sockets**

* https://www.baeldung.com/spring-5-reactive-websockets
* https://dzone.com/articles/build-a-chat-application-using-spring-boot-websock
