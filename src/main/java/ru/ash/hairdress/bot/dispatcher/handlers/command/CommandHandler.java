package ru.ash.hairdress.bot.dispatcher.handlers.command;

public interface CommandHandler {
    String getCommand();
    void handle(Long chatId);
}