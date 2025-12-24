package ru.ash.hairdress.bot.dispatcher.handlers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.sender.MessageSender;

@Component
@Slf4j
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {
    @Autowired
    private MessageSender sender;

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public void handle(Long chatId, boolean override) {
        String message = """
    Доступные команды:
            /start - Начать работу
            /clients - Показать список пользователей
            /help - Показать справку
    """;
        sender.sendMessage(chatId, message, null, override);
    }
}
