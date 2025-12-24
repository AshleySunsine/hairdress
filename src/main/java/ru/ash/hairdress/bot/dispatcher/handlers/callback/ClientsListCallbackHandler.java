package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.dispatcher.handlers.command.ClientsListCommandHandler;

@Component
@RequiredArgsConstructor
public class ClientsListCallbackHandler implements CallbackHandler {
    private final ClientsListCommandHandler clientsListCallbackHandler;

    @Override
    public String getCallback() {
        return "LIST_CLIENTS";
    }

    @Override
    public void handle(Long chatId, boolean override) {
        clientsListCallbackHandler.handle(chatId, override);
    }
}
