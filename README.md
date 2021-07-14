# Reactive Chat

## :computer: Build project

### Prerequisites
Node + Angular CLI
```shell
brew install node
npm install -g @angular/cli
```

### Project
Build & test
```shell
./gradlew build
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

## Useful links

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
