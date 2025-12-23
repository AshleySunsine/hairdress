package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.dispatcher.service.dialog.CreateClientDialogService;

@Component
@RequiredArgsConstructor
public class CreateClientCallbackHandler implements CallbackHandler {
    private final CreateClientDialogService dialogService;

    @Override
    public String getCallback() {
        return "CREATE_CLIENT";
    }

    @Override
    public void handle(Long chatId) {
        dialogService.startDialog(chatId);
    }
}
