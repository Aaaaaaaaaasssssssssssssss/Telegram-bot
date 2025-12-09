package com.example.bot


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import kotlin.system.exitProcess

fun main() {
    val token = System.getenv("TELEGRAM_BOT_TOKEN") ?: run {
        println("❌ ERROR: TELEGRAM_BOT_TOKEN environment variable is not set")
        println("🔧 Please set it in Render dashboard -> Environment")
        exitProcess(1)
    }
    
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    
    // Запуск HTTP сервера для health check
    startHealthServer(port)
    
    try {
        val botApplication = TelegramBotsLongPollingApplication()
        botApplication.registerBot(token, TelegramBot(token))
        
        println("✅ Bot successfully started on Render!")
        println("🌐 Health check available at: http://localhost:$port/health")
        println("🤖 Bot username: @${TelegramBot(token).botUsername}")
        
        // Keep the application running
        while (true) {
            Thread.sleep(1000)
        }
    } catch (e: TelegramApiException) {
        println("❌ Failed to start bot: ${e.message}")
        exitProcess(1)
    } catch (e: Exception) {
        println("❌ Unexpected error: ${e.message}")
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
                call.respondText("""
                    Telegram Bot is running! 🤖
                    
                    Endpoints:
                    - GET /health - Health check
                    - GET /status - Bot status
                    
                    Made with ❤️ on Render
                """.trimIndent())
            }
            
            get("/health") {
                call.respondText("OK")
            }
            
            get("/status") {
                call.respondText("""
                    Bot Status: ✅ Active
                    Platform: Render
                    Java: ${System.getProperty("java.version")}
                    Memory: ${Runtime.getRuntime().maxMemory() / 1024 / 1024} MB
                """.trimIndent())
            }
        }
    }
    
    // Запуск в отдельном потоке
    Thread {
        server.start(wait = true)
    }.start()
}
