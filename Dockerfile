# build
FROM openjdk:17-oracle AS builder
LABEL stage=builder
WORKDIR application

ARG JAR_FILE="build/libs/reactive-chat.jar"

ADD ${JAR_FILE} ./application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# image
FROM openjdk:17-oracle
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENV CONTEXT_PATH=""

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]