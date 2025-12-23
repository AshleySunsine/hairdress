package ru.ash.hairdress.bot.dispatcher.handlers.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.sender.MessageSender;
import ru.ash.hairdress.model.User;
import ru.ash.hairdress.service.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UsersCommandHandler implements CommandHandler {
    private final UserService userService;
    private final MessageSender sender;

    @Override
    public String getCommand() {
        return "/users";
    }

    @Override
    public void handle(Long chatId) {
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            sender.sendMessage(chatId, "📭 В базе данных нет пользователей.");
            return;
        }

        StringBuilder response = new StringBuilder("📋 Список пользователей:\n\n");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            response.append(i + 1)
                    .append(". ")
                    .append(user.getName())
                    .append(" (ID: ")
                    .append(user.getId())
                    .append(")\n");
        }

        response.append("\nВсего пользователей: ").append(users.size());
        sender.sendMessage(chatId, response.toString());
    }
}