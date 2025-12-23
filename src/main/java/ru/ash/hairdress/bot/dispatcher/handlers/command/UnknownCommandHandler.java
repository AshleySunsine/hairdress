package ru.ash.hairdress.bot.dispatcher.handlers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.sender.MessageSender;

@Component
@Slf4j
@RequiredArgsConstructor
public class UnknownCommandHandler implements CommandHandler {
    @Autowired
    private final MessageSender sender;

    @Override
    public String getCommand() {
        return "/unknown";
    }

    @Override
    public void handle(Long chatId) {
        String welcomeMessage = """
            👋 Я получил неизвестную команду.
            Доступные команды:
            /start - Начать работу
            /users - Показать список пользователей
            /help - Показать справку
            """;
        sender.sendMessage(chatId, welcomeMessage);
    }
}
