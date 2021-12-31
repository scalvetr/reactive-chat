# build
FROM ubuntu:20.04 AS builder

RUN apt install curl -y
RUN curl -fsSL https://deb.nodesource.com/setup_17.x | sudo -E bash -

RUN apt-get update;\
apt-get -y install nodejs;\
npm install -g npm@7.20.3;\
npm install -g @angular/cli@12.1.4;\
npm install -g typescript@4.3.5

RUN apt install openjdk-17-jre-headless -y
WORKDIR application

ADD . .
RUN ./gradlew clean build
RUN cp build/*.jar application.jar
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