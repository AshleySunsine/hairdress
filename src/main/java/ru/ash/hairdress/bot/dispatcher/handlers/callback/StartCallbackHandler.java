package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.dispatcher.handlers.command.StartCommandHandler;

@Component
@RequiredArgsConstructor
public class StartCallbackHandler implements CallbackHandler {
    private final StartCommandHandler startCommandHandler;

    @Override
    public String getCallback() {
        return "START";
    }

    @Override
    public void handle(Long chatId, boolean override) {
        startCommandHandler.handle(chatId, override);
    }
}
