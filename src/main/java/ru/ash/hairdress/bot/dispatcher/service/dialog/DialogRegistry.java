package ru.ash.hairdress.bot.dispatcher.service.dialog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DialogRegistry {
    private final List<DialogService> dialogServices;

    public Optional<DialogService> findActiveDialog(Long chatId) {
        return dialogServices.stream()
                .filter(dialog -> dialog.isUserInDialog(chatId))
                .findFirst();
    }

    public void cancelAllDialogs(Long chatId) {
        dialogServices.forEach(dialog -> dialog.cancelDialog(chatId));
    }
}
