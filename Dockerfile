FROM openjdk:17-alpine:latest

RUN mkdir -p /jseckill

WORKDIR /jseckill

ARG JAR_FILE=target/jseckill.jar

COPY ${JAR_FILE} app.jar

EXPOSE 9071

ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

CMD sleep 60; java $JAVA_OPTS -jar app.jar
