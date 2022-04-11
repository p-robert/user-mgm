FROM maven:3.6.0-jdk-11-slim AS builder
RUN mkdir -p /app/user-mgm

WORKDIR /app/user-mgm
ADD . /app/user-mgm

RUN mvn clean install -DskipTests -DskipIT
RUN ls -la /app/user-mgm/target/

FROM adoptopenjdk/openjdk11:jre-11.0.3_7-alpine
RUN mkdir -p /app
COPY --from=builder /app/user-mgm/target/*.jar /app/user-mgm.jar
WORKDIR /app

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/user-mgm.jar"]