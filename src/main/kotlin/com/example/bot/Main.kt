package com.example.bot

import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import com.theokanning.openai.service.OpenAiService
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration

fun main() {
    // Запускаем health check в отдельном потоке
    Thread {
        startHealthServer()
    }.apply {
        isDaemon = true
        start()
    }
    
    // Даем время health серверу запуститься
    Thread.sleep(2000)
    
    try {
        val openAiService = OpenAiService(System.getenv("DEEPSEEK_API_KEY") ?: throw IllegalArgumentException("DEEPSEEK_API_KEY not set"), Duration.ofSeconds(30), "https://api.deepseek.com/v1")
        
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        val bot = TelegramBot(openAiService)
        botsApi.registerBot(bot)
        
        println("✅ Bot started with Java ${System.getProperty("java.version")}")
        
        // Держим приложение живым
        Thread.currentThread().join()
    } catch (e: Exception) {
        println("❌ Failed to start bot: ${e.message}")
        e.printStackTrace()
        System.exit(1)
    }
}

fun startHealthServer() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    
    try {
        // Простой HTTP сервер на чистом Java
        val server = com.sun.net.httpserver.HttpServer.create(
            java.net.InetSocketAddress(port), 0
        )
        
        server.createContext("/health") { exchange ->
            val response = "OK"
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        
        server.createContext("/") { exchange ->
            val response = """
                Telegram Bot Status: ONLINE
                Java: ${System.getProperty("java.version")}
                Memory: ${Runtime.getRuntime().totalMemory() / 1024 / 1024}MB
            """.trimIndent()
            exchange.sendResponseHeaders(200, response.length.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        
        server.executor = null // Используем текущий поток
        server.start()
        println("🏥 Health server started on port $port")
    } catch (e: Exception) {
        println("⚠️ Health server failed: ${e.message}")
        // Не падаем, если health server не запустился
    }
}
