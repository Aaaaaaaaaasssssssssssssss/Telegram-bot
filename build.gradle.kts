plugins {
    kotlin("jvm") version "1.9.23"  // Используем стабильную версию
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Раздельные зависимости - работают лучше
    implementation("org.telegram:telegrambots-longpolling:7.9.0")
    implementation("org.telegram:telegrambots-client:7.9.0")
    
    // Ktor (если нужен)
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

// Простой JAR без Shadow плагина
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.bot.MainKt"]
    }
    from(configurations.runtimeClasspath.get().map { 
        if (it.isDirectory) it else zipTree(it) 
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
