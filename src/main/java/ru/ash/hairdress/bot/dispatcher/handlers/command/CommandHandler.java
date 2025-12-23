package ru.ash.hairdress.bot.dispatcher.handlers.command;

import ru.ash.hairdress.bot.dispatcher.handlers.ActionHandler;

public interface CommandHandler extends ActionHandler {
    String getCommand();
}