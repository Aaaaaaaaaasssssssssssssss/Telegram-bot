# Сборка
FROM gradle:8.5-jdk21-alpine AS build

WORKDIR /app

COPY src ./src
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN gradle clean jar --no-daemon

RUN mv build/libs/*.jar app.jar

# Запуск - ИСПРАВЛЕНО: правильный тег
FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY --from=build /app/app.jar /app/bot.jar

EXPOSE 8080

CMD ["java", "-jar", "bot.jar"]
