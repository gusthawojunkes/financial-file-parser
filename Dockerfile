FROM amazoncorretto:21

WORKDIR /api

COPY gradlew .
COPY gradle gradle
COPY src src
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle.properties .
COPY src/test/resources/files .

RUN chmod +x gradlew

RUN ./gradlew build

EXPOSE ${PORT}:8080

CMD ["./gradlew", "run"]