package ru.ash.hairdress.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ash.hairdress.bot.dispatcher.handlers.callback.CallbackDispatcher;
import ru.ash.hairdress.bot.dispatcher.handlers.command.CommandDispatcher;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserListBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final CommandDispatcher dispatcher;
    private final CallbackDispatcher callbackDispatcher;


    @Autowired
    public UserListBot(@org.springframework.beans.factory.annotation.Value("${telegram.bot.username}") String botUsername,
                       @org.springframework.beans.factory.annotation.Value("${telegram.bot.token}") String botToken,
                       CommandDispatcher dispatcher, CallbackDispatcher callbackDispatcher) {
        super(botToken);
        this.botUsername = botUsername;
        this.dispatcher = dispatcher;
        this.callbackDispatcher = callbackDispatcher;
        initializeCommands();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    private void initializeCommands() {
        try {
            List<BotCommand> commands = new ArrayList<>();
            commands.add(new BotCommand("/start", "Начать работу с ботом"));
            commands.add(new BotCommand("/clients", "Получить список пользователей"));
            commands.add(new BotCommand("/help", "Показать справку"));

            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Обработка команд (/start, /clients и т.д.)
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            dispatcher.dispatch(messageText, chatId);

        } else if (update.hasCallbackQuery()) {
            // Обработка нажатий инлайн-кнопок
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            Long chatId = callbackQuery.getMessage().getChatId();

            callbackDispatcher.dispatch(callbackData, chatId);
        }
    }
}
