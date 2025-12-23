package ru.ash.hairdress.bot.dispatcher.service.dialog;

public interface DialogService {
    boolean isUserInDialog(Long chatId);
    boolean processInput(Long chatId, String input);
    boolean processCallback(Long chatId, String callbackData); // Для кнопок в диалоге
    void cancelDialog(Long chatId);
    void startDialog(Long chatId);
}
