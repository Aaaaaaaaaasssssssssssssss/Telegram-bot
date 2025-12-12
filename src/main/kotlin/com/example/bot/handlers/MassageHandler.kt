package com.example.bot.handlers

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

class MessageHandler {
    
    fun handleMessage(message: Message, bot: TelegramLongPollingBot) {
        val chatId = message.chatId.toString()
        val text = message.text ?: ""
        
        val response = when {
            text.contains("привет", ignoreCase = true) -> "И тебе привет!"
            text.contains("как дела", ignoreCase = true) -> "У меня всё отлично! А у тебя?"
            text.contains("Ужас", ignoreCase = true) -> "Бррр, страшно!"
            else -> ""
        }
        
        val sendMessage = SendMessage(chatId, response)
        bot.execute(sendMessage)
    }
}
