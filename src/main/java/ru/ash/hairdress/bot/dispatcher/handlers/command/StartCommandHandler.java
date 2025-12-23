package ru.ash.hairdress.bot.dispatcher.handlers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.ash.hairdress.bot.sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    @Autowired
    private final MessageSender sender;

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void handle(Long chatId) {
        String welcomeMessage = "👋 Выберите действие:";

        // Создаем инлайн-клавиатуру
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Первый ряд кнопок
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("➕ Добавить пользователя")
                .callbackData("CREATE_USER")
                .build());

        // Второй ряд кнопок
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("🗑 Удалить пользователя")
                .callbackData("DELETE_USER")
                .build());

        rows.add(row1);
        rows.add(row2);
        keyboard.setKeyboard(rows);

        sender.sendMessageWithKeyboard(chatId, welcomeMessage, keyboard);
    }
}
