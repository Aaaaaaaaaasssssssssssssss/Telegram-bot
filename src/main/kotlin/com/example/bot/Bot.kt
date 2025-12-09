package com.example.bot

import com.example.bot.handlers.CommandHandler
import com.example.bot.handlers.MessageHandler
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class TelegramBot : TelegramLongPollingBot() {
    
    private val commandHandler = CommandHandler()
    private val messageHandler = MessageHandler()
    
    init {
        setupCommands()
    }
    
    override fun getBotUsername(): String = "RenderBot"
    
    override fun getBotToken(): String {
        return System.getenv("TELEGRAM_BOT_TOKEN") ?: ""
    }
    
    override fun onUpdateReceived(update: Update) {
        try {
            when {
                update.hasMessage() && update.message.hasText() -> {
                    val message = update.message
                    
                    // Если сообщение - команда
                    if (message.text.startsWith("/")) {
                        commandHandler.handleCommand(message, this)
                    } else {
                        messageHandler.handleMessage(message, this)
                    }
                }
                
                update.hasCallbackQuery() -> {
                    commandHandler.handleCallbackQuery(update.callbackQuery, this)
                }
            }
        } catch (e: Exception) {
            println("Error processing update: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun setupCommands() {
        try {
            val commands = listOf(
                BotCommand("start", "Запустить бота"),
                BotCommand("help", "Помощь"),
                BotCommand("echo", "Повторить текст")
            )
            
            execute(SetMyCommands(commands, BotCommandScopeDefault(), null))
        } catch (e: TelegramApiException) {
            println("Failed to set commands: ${e.message}")
        }
    }
}
