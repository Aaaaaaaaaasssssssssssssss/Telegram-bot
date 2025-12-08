plugins {
    kotlin("jvm") version "2.1.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"  // Для создания fat JAR
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Telegram Bot API
    implementation("org.telegram:telegrambots-longpolling:7.9.0")
    implementation("org.telegram:telegrambots-client:7.9.0")
    
    // Для веб-сервера (если нужны дополнительные эндпоинты)
    implementation("io.ktor:ktor-server-core:2.3.10")
    implementation("io.ktor:ktor-server-netty:2.3.10")
    implementation("io.ktor:ktor-server-status-pages:2.3.10")
    
    // Логирование
    implementation("ch.qos.logback:logback-classic:1.5.6")
    
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.example.bot.MainKt")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("telegram-bot")
        archiveClassifier.set("")
        archiveVersion.set("")
        manifest {
            attributes(mapOf("Main-Class" to "com.example.bot.MainKt"))
        }
    }
    
    build {
        dependsOn(shadowJar)
    }
}