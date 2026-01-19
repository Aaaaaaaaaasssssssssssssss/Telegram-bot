# Сборка
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

COPY src ./src
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .
COPY system.properties .

RUN gradle clean jar --no-daemon

RUN mv build/libs/*.jar app.jar

# Запуск - ИСПРАВЛЕНО: правильный тег
FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY --from=build /app/app.jar /app/bot.jar

# Переменные окружения для токенов
ENV TELEGRAM_BOT_TOKEN=""
ENV OPENAI_API_KEY=""

EXPOSE 8080

CMD ["java", "-jar", "bot.jar"]
