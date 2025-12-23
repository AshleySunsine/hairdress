package ru.ash.hairdress.bot.dispatcher.service.dialog;

public interface DialogService {
    boolean isUserInDialog(Long chatId);
    boolean processInput(Long chatId, String input);
    void cancelDialog(Long chatId);
    void startDialog(Long chatId);
}
