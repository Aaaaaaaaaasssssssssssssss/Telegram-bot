# Сборка с глобальным Gradle
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# 1. Копируем исходники
COPY src ./src
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 2. Собираем с глобальным gradle (не wrapper)
RUN gradle clean jar --no-daemon

# 3. Переименовываем для удобства
RUN mv build/libs/*.jar app.jar

# Запуск
FROM openjdk:21-slim
COPY --from=build /app/app.jar /app/bot.jar
WORKDIR /app
CMD ["java", "-jar", "bot.jar"]
