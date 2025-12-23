package ru.ash.hairdress.bot.dispatcher.handlers.callback;

public interface CallbackHandler {
    String getCallback();
    void handle(Long chatId);
}
