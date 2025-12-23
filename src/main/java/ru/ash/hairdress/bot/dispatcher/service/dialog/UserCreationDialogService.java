package ru.ash.hairdress.bot.dispatcher.service.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ash.hairdress.bot.sender.MessageSender;
import ru.ash.hairdress.model.User;
import ru.ash.hairdress.service.UserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserCreationDialogService implements DialogService{
    private final UserService userService;
    private final MessageSender sender;
    private final Map<Long, DialogState> states = new ConcurrentHashMap<>();

    private static class DialogState {
        private String name;
        private String email;
    }

    @Override
    public void startDialog(Long chatId) {
        states.put(chatId, new DialogState());
        sender.sendMessage(chatId, "Введите имя пользователя:");
    }

    public boolean processInput(Long chatId, String input) {
        DialogState state = states.get(chatId);
        if (state == null) return false;

        if (state.name == null) {
            state.name = input;
            sender.sendMessage(chatId, "Введите email:");
            return true;
        } else if (state.email == null) {
            state.email = input;
            createUser(chatId, state);
            states.remove(chatId);
            return true;
        }
        return false;
    }

    private void createUser(Long chatId, DialogState state) {
        User user = new User();
        user.setName(state.name);
        user.setEmail(state.email);
        userService.createUser(user);
        sender.sendMessage(chatId, "✅ Пользователь " + state.name + " создан!");
    }

    public boolean isUserInDialog(Long chatId) {
        return states.containsKey(chatId);
    }

    public void cancelDialog(Long chatId) {
        states.remove(chatId);
    }
}
