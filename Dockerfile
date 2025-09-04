FROM amazoncorretto:21 AS builder

WORKDIR /home/gradle

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

COPY gradlew .
RUN chmod +x ./gradlew

COPY src ./src

RUN ./gradlew shadowJar --no-daemon

FROM amazoncorretto:21

WORKDIR /app

COPY --from=builder /home/gradle/build/libs/*.jar ./app.jar
COPY --from=builder /home/gradle/src/main/resources/ ./resources/

EXPOSE 8080

CMD ["java", "-cp", "app.jar:resources/", "dev.wo.ApplicationKt"]
