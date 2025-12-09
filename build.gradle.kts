plugins {
    kotlin("jvm") version "1.9.22" // Совместима с Java 21
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.example"
version = "1.0.0"

application {
    mainClass.set("com.example.bot.MainKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Telegram Bot API (проверенная версия)
    implementation("org.telegram:telegrambots:6.9.7.1")
    
    // Ktor с минимальными зависимостями
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-status-pages:2.3.7")
    
    // Логирование
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "21"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    
    // Настраиваем shadowJar для минимального размера
    shadowJar {
        archiveBaseName.set("bot")
        archiveClassifier.set("")
        archiveVersion.set("")
        manifest {
            attributes["Main-Class"] = "com.example.bot.MainKt"
        }
        
        // Минимизируем размер
        minimize {
            exclude(dependency("org.telegram:telegrambots:.*"))
        }
        
        // Исключаем ненужные файлы
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        exclude("**/module-info.class")
    }
    
    // Оптимизация сборки для CI/CD
    build {
        dependsOn(shadowJar)
    }
}
