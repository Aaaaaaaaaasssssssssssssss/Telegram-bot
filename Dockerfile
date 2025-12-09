# Этап 1: Сборка с кэшированием зависимостей
FROM gradle:8.5-jdk21-alpine AS build

# Устанавливаем минимальные настройки памяти для Gradle
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx256m -Xms128m -XX:MaxMetaspaceSize=128m"
ENV GRADLE_USER_HOME=/home/gradle/.gradle

WORKDIR /app

# 1. Копируем файлы для кэширования зависимостей
COPY gradlew gradlew.bat ./
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle.kts settings.gradle.kts ./

# 2. Даем права на выполнение gradlew
RUN chmod +x gradlew

# 3. Скачиваем зависимости (кешируем этот слой)
RUN ./gradlew dependencies --no-daemon || true

# 4. Копируем исходный код
COPY src ./src

# 5. Собираем приложение
RUN ./gradlew clean shadowJar --no-daemon --stacktrace --info

# Этап 2: Запуск
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем ограничение памяти для JVM
ENV JAVA_OPTS="-Xmx128m -Xms64m -XX:MaxRAM=256m -XX:+UseSerialGC -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

WORKDIR /app

# 1. Создаем не-root пользователя для безопасности
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 2. Копируем собранный JAR
COPY --from=build /app/build/libs/*.jar app.jar

# 3. Healthcheck для Render
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# 4. Открываем порт (Render сам устанавливает PORT)
EXPOSE 8080

# 5. Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
