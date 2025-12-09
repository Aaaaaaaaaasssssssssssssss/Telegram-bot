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
    
    init {
        setupCommands()
    }
    
    override fun getBotToken(): String = token
    
    override fun getBotUsername(): String = "RenderBot"
    
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
            text.startsWith("/start") -> "🚀 Бот запущен на Render!"
            text.startsWith("/help") -> "📚 Используй /start, /status"
            else -> "🤔 Используй /help"
        }
        
        sendMessage(chatId, response)
    }
    
    private fun sendMessage(chatId: String, text: String) {
        try {
            val message = SendMessage(chatId, text)
            execute(message)
        } catch (e: TelegramApiException) {
            println("Failed to send message: ${e.message}")
        }
    }
    
    private fun setupCommands() {
        try {
            val commands = listOf(
                BotCommand("start", "Запустить бота"),
                BotCommand("help", "Помощь")
            )
            
            execute(SetMyCommands(commands, BotCommandScopeDefault(), null))
        } catch (e: Exception) {
            println("Failed to set commands: ${e.message}")
        }
    }
}
