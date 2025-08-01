FROM amazoncorretto:21 AS builder

WORKDIR /home/gradle

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

COPY gradlew .
RUN chmod +x ./gradlew

RUN ./gradlew build --no-daemon

COPY src ./src

RUN ./gradlew build --no-daemon

FROM amazoncorretto:21

WORKDIR /app

COPY --from=builder /home/gradle/build/libs/*.jar ./

EXPOSE 8080

CMD ["java", "-Dserver.port=$PORT", "-jar", "financial-file-parser.jar"]
