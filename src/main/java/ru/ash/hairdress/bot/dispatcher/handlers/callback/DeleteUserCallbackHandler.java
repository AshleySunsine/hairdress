package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.sender.MessageSender;

@Component
@RequiredArgsConstructor
public class DeleteUserCallbackHandler implements CallbackHandler {
    private final MessageSender sender;

    @Override
    public String getCallback() {
        return "DELETE_USER";
    }

    @Override
    public void handle(Long chatId) {
        sender.sendMessage(chatId, "Функция удаления в разработке");
    }
}