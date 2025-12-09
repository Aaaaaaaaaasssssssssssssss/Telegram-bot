package com.example.bot

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import kotlin.system.exitProcess

fun main() {
    val token = System.getenv("TELEGRAM_BOT_TOKEN") ?: run {
        println("❌ ERROR: TELEGRAM_BOT_TOKEN environment variable is not set")
        println("🔧 Please set it in Render dashboard -> Environment")
        exitProcess(1)
    }
    
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    
    startHealthServer(port)
    
    try {
        // СТАРЫЙ API
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        val bot = TelegramBot() // Создаем бота (токен берется из переменных окружения внутри класса)
        botsApi.registerBot(bot)
        
        println("✅ Bot successfully started on Render!")
        println("🌐 Health check available at: http://localhost:$port/health")
        println("🤖 Bot username: @${bot.botUsername}")
        
        while (true) {
            Thread.sleep(1000)
        }
    } catch (e: Exception) {
        println("❌ Failed to start bot: ${e.message}")
        e.printStackTrace()
        exitProcess(1)
    }
}

fun startHealthServer(port: Int) {
    val server = embeddedServer(Netty, port = port) {
        install(StatusPages) {
            status(HttpStatusCode.NotFound) { call, _ ->
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
        
        routing {
            get("/") {
                call.respondText("Telegram Bot is running! 🤖")
            }
            
            get("/health") {
                call.respondText("OK")
            }
        }
    }
    
    server.start(wait = false)
}                
