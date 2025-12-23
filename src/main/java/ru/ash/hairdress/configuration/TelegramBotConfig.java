package ru.ash.hairdress.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.ash.hairdress.bot.UserListBot;
import ru.ash.hairdress.bot.dispatcher.handlers.callback.CallbackDispatcher;
import ru.ash.hairdress.bot.dispatcher.handlers.command.CommandDispatcher;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Bean
    public UserListBot userListBot(CommandDispatcher dispatcher, CallbackDispatcher callbackDispatcher) {
        return new UserListBot(botUsername, botToken, dispatcher, callbackDispatcher);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(UserListBot userListBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(userListBot);
        return botsApi;
    }
}
