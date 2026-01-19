package com.example.bot.handlers

import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class MessageHandler(private val openAiService: OpenAiService) {
    
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
        
        // Отвечаем через GPT-4
        try {
            val chatRequest = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(listOf(ChatMessage(ChatMessageRole.USER.value(), text)))
                .build()
            
            val response = openAiService.createChatCompletion(chatRequest)
            val reply = response.choices[0].message.content
            
            sendResponse(chatId, reply, bot)
        } catch (e: Exception) {
            println("❌ GPT error: ${e.message}")
            sendResponse(chatId, "Извините, произошла ошибка при обработке вашего сообщения.", bot)
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
