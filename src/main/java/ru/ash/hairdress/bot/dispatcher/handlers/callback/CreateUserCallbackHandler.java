package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.dispatcher.service.dialog.UserCreationDialogService;

@Component
@RequiredArgsConstructor
public class CreateUserCallbackHandler implements CallbackHandler {
    private final UserCreationDialogService dialogService;

    @Override
    public String getCallback() {
        return "CREATE_USER";
    }

    @Override
    public void handle(Long chatId) {
        dialogService.startDialog(chatId);
    }
}
