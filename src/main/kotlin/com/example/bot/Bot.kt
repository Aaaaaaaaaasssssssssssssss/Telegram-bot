package com.example.bot

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramBot

class TelegramBot(private val token: String) : LongPollingSingleThreadUpdateConsumer, TelegramBot {
    
    val botUsername: String = "RenderBot"
    
    init {
        setupCommands()
    }
    
    override fun getBotToken(): String = token
    
    override fun getBotUsername(): String = botUsername
    
    override fun consume(update: Update) {
        try {
            if (update.hasMessage() && update.message.hasText()) {
                handleMessage(update.message)
            }
        } catch (e: Exception) {
            println("Error processing update: ${e.message}")
        }
    }
    
    private fun handleMessage(message: org.telegram.telegrambots.meta.api.objects.Message) {
        val chatId = message.chatId.toString()
        val text = message.text ?: ""
        
        val response = when {
            text.startsWith("/start") -> """
                🚀 Добро пожаловать!
                
                Я бот, работающий на Render.com!
                
                Команды:
                /help - Помощь
                /status - Статус бота
                /info - Информация
                
                💡 Бот автоматически обновляется из GitHub!
            """.trimIndent()
            
            text.startsWith("/help") -> """
                📚 Доступные команды:
                
                /start - Начало работы
                /help - Эта справка
                /status - Статус системы
                /info - Информация о боте
                /echo [текст] - Повторить текст
                
                🛠️ Hosted on Render.com
            """.trimIndent()
            
            text.startsWith("/status") -> """
                📊 Статус системы:
                
                • Платформа: Render.com
                • Java: ${System.getProperty("java.version")}
                • Память: ${Runtime.getRuntime().freeMemory() / 1024 / 1024} MB свободно
                • Время: ${java.time.LocalDateTime.now()}
                
                ✅ Все системы работают нормально
            """.trimIndent()
            
            text.startsWith("/info") -> """
                🤖 Информация о боте:
                
                • Имя: @$botUsername
                • Хостинг: Render.com
                • Режим: Long Polling
                • Авто-деплой: включен
                
                📦 Исходный код: GitHub
                🔄 Авто-обновление: при пуше в main
            """.trimIndent()
            
            text.startsWith("/echo ") -> {
                val echoText = text.substringAfter("/echo ").trim()
                if (echoText.isNotEmpty()) echoText else "Что повторить?"
            }
            
            else -> "🤔 Команда не распознана. Используй /help"
        }
        
        sendMessage(chatId, response)
    }
    
    private fun sendMessage(chatId: String, text: String) {
        try {
            val message = SendMessage(chatId, text)
            message.enableHtml(true)
            execute(message)
        } catch (e: TelegramApiException) {
            println("Failed to send message: ${e.message}")
        }
    }
    
    private fun setupCommands() {
        try {
            val commands = listOf(
                BotCommand("start", "Запустить бота"),
                BotCommand("help", "Помощь"),
                BotCommand("status", "Статус системы"),
                BotCommand("info", "Информация"),
                BotCommand("echo", "Повторить текст")
            )
            
            execute(SetMyCommands(commands, BotCommandScopeDefault(), null))
        } catch (e: Exception) {
            println("Failed to set commands: ${e.message}")
        }
    }
}                
