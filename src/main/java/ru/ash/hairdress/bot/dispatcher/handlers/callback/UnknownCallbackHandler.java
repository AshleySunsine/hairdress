package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.sender.MessageSender;

@Component
@RequiredArgsConstructor
public class UnknownCallbackHandler implements CallbackHandler {
    private final MessageSender sender;

    @Override
    public String getCallback() {
        return "UNKNOWN"; // Специальный ключ для неизвестных колбэков
    }

    @Override
    public void handle(Long chatId) {
        sender.sendMessage(chatId, "❌ Неизвестное действие. Используйте меню.");
    }
}