// build.gradle.kts
plugins {
    kotlin("jvm") version "1.9.22"
    application
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
    // Только самое необходимое
    implementation("org.telegram:telegrambots:6.9.7.1")
    
    // Минимальные HTTP зависимости вместо Ktor
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Логирование
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }
    
    jar {
        manifest {
            attributes["Main-Class"] = "com.example.bot.MainKt"
        }
        from(configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
