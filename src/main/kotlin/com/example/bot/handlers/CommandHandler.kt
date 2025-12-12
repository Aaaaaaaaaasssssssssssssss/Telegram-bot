package com.example.bot.handlers

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class CommandHandler {
    
    fun handleCommand(message: Message, bot: TelegramLongPollingBot) {
        val chatId = message.chatId.toString()
        val text = message.text ?: ""
        
        // Проверяем на опасные команды
        if (isDangerousCommand(text)) {
            println("⚠️ Blocked dangerous command from ${message.from.userName}")
            return
        }
        
        val response = when {
            text.startsWith("/start") -> {
                """
                👋 Привет! Я минималистичный бот.
                
                Я отвечаю на:
                • "привет"
                • "как дела"
                • "ужас"
                
                И понимаю команды:
                /start - это сообщение
                /help - список команд
                /echo [текст] - повтор текста
                """.trimIndent()
            }
            text.startsWith("/help") -> {
                """
                📋 Доступные команды:
                /start - начало работы
                /help - эта справка
                /echo [текст] - повторить текст
                
                Также я отвечаю на слова:
                привет, как дела, ужас
                """.trimIndent()
            }
            text.startsWith("/echo") -> {
                val args = text.substringAfter("/echo").trim()
                if (args.isNotEmpty()) "📢 $args" else "❓ Что повторить?"
            }
            else -> "🤔 Неизвестная команда. Попробуй /help"
        }
        
        try {
            val sendMessage = SendMessage(chatId, response)
            bot.execute(sendMessage)
        } catch (e: TelegramApiException) {
            println("❌ Telegram API error: ${e.message}")
        } catch (e: Exception) {
            println("❌ Unexpected error: ${e.message}")
        }
    }
    
    private fun isDangerousCommand(text: String): Boolean {
        // Блокируем опасные команды
        val dangerousCommands = listOf(
            "/admin",
            "/root", 
            "/sudo",
            "/system",
            "/shell",
            "/exec"
        )
        
        return dangerousCommands.any { text.startsWith(it, ignoreCase = true) }
    }
    
    fun handleCallbackQuery(callbackQuery: CallbackQuery, bot: TelegramLongPollingBot) {
        // Без изменений
        val chatId = callbackQuery.message.chatId.toString()
        val data = callbackQuery.data ?: ""
        
        val response = "Вы нажали: $data"
        
        try {
            val sendMessage = SendMessage(chatId, response)
            bot.execute(sendMessage)
        } catch (e: Exception) {
            println("❌ Error handling callback: ${e.message}")
        }
    }
}
