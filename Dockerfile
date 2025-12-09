FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .

# Используем --scan для детального отчета
RUN gradle build --no-daemon --stacktrace --debug --info

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
