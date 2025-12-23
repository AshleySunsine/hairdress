package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import ru.ash.hairdress.bot.dispatcher.handlers.ActionHandler;

public interface CallbackHandler extends ActionHandler {
    String getCallback();
}
