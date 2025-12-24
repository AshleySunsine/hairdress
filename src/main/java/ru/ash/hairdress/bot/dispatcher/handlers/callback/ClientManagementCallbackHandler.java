package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.ash.hairdress.bot.sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientManagementCallbackHandler implements CallbackHandler {
    private final MessageSender sender;

    @Override
    public String getCallback() {
        return "CLIENT_MANAGEMENT";
    }

    @Override
    public void handle(Long chatId, boolean override) {
        String message = "👥 *Управление клиентами*";

        InlineKeyboardMarkup keyboard = createClientManagementKeyboard();
        sender.sendMessage(chatId, message, keyboard, override);
    }

    private InlineKeyboardMarkup createClientManagementKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("➕ Добавить клиента")
                .callbackData("CREATE_CLIENT")
                .build());

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("✏️ Редактировать клиента")
                .callbackData("EDIT_CLIENT")
                .build());

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(InlineKeyboardButton.builder()
                .text("🗑 Удалить клиента")
                .callbackData("DELETE_CLIENT")
                .build());

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(InlineKeyboardButton.builder()
                .text("📋 Список клиентов")
                .callbackData("LIST_CLIENTS")
                .build());

        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(InlineKeyboardButton.builder()
                .text("🔍 Найти клиента")
                .callbackData("SEARCH_CLIENT")
                .build());

        List<InlineKeyboardButton> row6 = new ArrayList<>();
        row6.add(InlineKeyboardButton.builder()
                .text("◀️ Назад")
                .callbackData("START")
                .build());

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
