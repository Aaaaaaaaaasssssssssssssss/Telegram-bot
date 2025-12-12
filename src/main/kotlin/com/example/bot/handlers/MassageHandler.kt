package com.example.bot.handlers

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class MessageHandler {
    
    fun handleMessage(message: Message, bot: TelegramLongPollingBot) {
        val chatId = message.chatId.toString()
        val text = message.text ?: ""
        
        // Пропускаем пустые сообщения
        if (text.trim().isEmpty()) {
            return
        }
        
        // Проверяем на опасные паттерны - просто игнорируем их
        if (isDangerousInput(text)) {
            println("⚠️ Ignoring dangerous input from ${message.from.userName}: ${text.take(50)}...")
            return
        }
        
        // Отвечаем ТОЛЬКО на определенные фразы
        val response = when {
            text.contains("привет", ignoreCase = true) -> "И тебе привет! 👋"
            text.contains("как дела", ignoreCase = true) -> "У меня всё отлично! А у тебя? 😊"
            text.contains("ужас", ignoreCase = true) -> "Бррр, страшно! 😨"
            else -> null // Не отвечаем на другие сообщения
        }
        
        // Отправляем ответ только если он есть
        response?.let {
            sendResponse(chatId, it, bot)
        }
    }
    
    private fun sendResponse(chatId: String, text: String, bot: TelegramLongPollingBot) {
        try {
            val sendMessage = SendMessage(chatId, text)
            bot.execute(sendMessage)
        } catch (e: TelegramApiException) {
            println("❌ Telegram API error: ${e.message}")
        } catch (e: Exception) {
            println("❌ Unexpected error: ${e.message}")
        }
    }
    
    private fun isDangerousInput(text: String): Boolean {
        // Паттерны опасного ввода (SQL, XSS, команды и т.д.)
        val dangerousPatterns = listOf(
            Regex("[';]+|--+"),
            Regex("union.*select", RegexOption.IGNORE_CASE),
            Regex("drop.*table", RegexOption.IGNORE_CASE),
            Regex("\\$\\{.*\\}|\\(\\$.*\\)"),
            Regex(";\\s*(ls|cat|rm|sh|bash|python|perl)"),
            Regex("<script|onerror=|alert\\("),
            Regex("\\.\\./|\\.\\.\\\\"),
            Regex("\\{\\{.*\\}\\}"),
            Regex("eval\\(|exec\\(|runtime\\."),
            Regex("(curl|wget)\\s+"),
            Regex("(admin|root|sudo)\\s+", RegexOption.IGNORE_CASE)
        )
        
        return dangerousPatterns.any { it.containsMatchIn(text) }
    }
}
