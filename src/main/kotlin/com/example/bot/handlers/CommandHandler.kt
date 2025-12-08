package com.example.bot.handlers

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.CallbackQuery

class CommandHandler {
    
    fun handleCommand(message: Message, bot: TelegramLongPollingBot) {
        val chatId = message.chatId.toString()
        val text = message.text ?: ""
        
        val response = when {
            text.startsWith("/start") -> {
                "Привет! Я пример бота. Используй /help для списка команд."
            }
            text.startsWith("/help") -> {
                """
                Доступные команды:
                /start - начать работу
                /help - помощь
                /echo [текст] - повторить текст
                """.trimIndent()
            }
            text.startsWith("/echo") -> {
                val args = text.substringAfter("/echo").trim()
                if (args.isNotEmpty()) args else "Что повторить?"
            }
            else -> "Неизвестная команда. Используй /help"
        }
        
        val sendMessage = SendMessage(chatId, response)
        bot.execute(sendMessage)
    }
    
    fun handleCallbackQuery(callbackQuery: CallbackQuery, bot: TelegramLongPollingBot) {
        // Обработка нажатий на inline-кнопки
        val chatId = callbackQuery.message.chatId.toString()
        val data = callbackQuery.data
        
        val response = "Вы нажали: $data"
        val sendMessage = SendMessage(chatId, response)
        bot.execute(sendMessage)
    }
}