package ru.ash.hairdress.bot.dispatcher.handlers.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.ash.hairdress.bot.sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {
    private final MessageSender sender;

    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public void handle(Long chatId, boolean override) {
        String welcomeMessage = """
            ✂️ *Добро пожаловать в систему учёта парикмахерской!*
            
            Выберите действие:
            """;

        InlineKeyboardMarkup keyboard = createMainMenuKeyboard();
        sender.sendMessage(chatId, welcomeMessage, keyboard, override);
    }

    private InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("👥 Управление клиентами")
                .callbackData("CLIENT_MANAGEMENT")
                .build());

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("📅 Записи на услуги")
                .callbackData("APPOINTMENTS")
                .build());

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(InlineKeyboardButton.builder()
                .text("📊 Отчёты и статистика")
                .callbackData("REPORTS")
                .build());

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
