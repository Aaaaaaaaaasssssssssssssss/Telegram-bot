# Этап 1: Сборка
FROM gradle:8.5-jdk21 AS build

# Минимальные настройки памяти для Gradle (ВНИМАНИЕ: Xms < Xmx!)
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx256m -Xms64m -XX:MaxMetaspaceSize=128m -XX:+HeapDumpOnOutOfMemoryError"
ENV GRADLE_USER_HOME=/home/gradle/.gradle

WORKDIR /app

# 1. Копируем файлы для кэширования
COPY gradlew gradlew.bat ./
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle.kts settings.gradle.kts ./

# 2. Даем права на выполнение (ИСПРАВЛЕНО: chmod +x, не --x)
RUN chmod +x gradlew

# 3. Скачиваем зависимости с минимальной памятью
RUN ./gradlew dependencies --no-daemon --stacktrace || echo "Dependencies downloaded"

# 4. Копируем исходный код
COPY src ./src

# 5. Собираем приложение (ИСПРАВЛЕНО: shadowJar, не shadowlar)
RUN ./gradlew clean shadowJar --no-daemon --stacktrace --info

# Этап 2: Запуск
FROM eclipse-temurin:21-jre-alpine

# Память для production (Render Free: 512MB RAM)
ENV JAVA_OPTS="-Xmx128m -Xms64m -XX:MaxRAM=256m -XX:+UseSerialGC -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

WORKDIR /app

# 1. Создаем пользователя
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 2. Копируем JAR
COPY --from=build /app/build/libs/*.jar app.jar

# 3. Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# 4. Порт
EXPOSE 8080

# 5. Запуск
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
