package ru.ash.hairdress.bot.dispatcher.service.dialog;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.ash.hairdress.bot.dispatcher.handlers.ActionHandler;
import ru.ash.hairdress.bot.sender.MessageSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseDialogService implements DialogService {
    protected final MessageSender sender;
    protected final ActionHandler targetHandler;
    protected final Map<Long, DialogContext> contexts = new ConcurrentHashMap<>();

    protected BaseDialogService(MessageSender sender, ActionHandler targetHandler) {
        this.sender = sender;
        this.targetHandler = targetHandler;
    }

    // Абстрактные методы для реализации в конкретных диалогах
    protected abstract void onStart(Long chatId, DialogContext context);
    protected abstract boolean onProcessInput(Long chatId, String input, DialogContext context);
    protected abstract boolean onProcessCallback(Long chatId, String callbackData, DialogContext context);
    protected abstract void onCancel(Long chatId, DialogContext context);

    // Базовый контекст диалога
    protected static class DialogContext {
        private final Map<String, Object> data = new HashMap<>();
        private boolean awaitingCancelConfirmation = false;

        public void put(String key, Object value) { data.put(key, value); }
        public <T> T get(String key) { return (T) data.get(key); }
        public boolean has(String key) { return data.containsKey(key); }
    }

    // Реализация интерфейса DialogService
    @Override
    public final void startDialog(Long chatId) {
        DialogContext context = new DialogContext();
        contexts.put(chatId, context);
        onStart(chatId, context);
        showCancelButton(chatId, getCurrentPrompt(chatId, context));
    }

    @Override
    public final boolean processInput(Long chatId, String input) {
        DialogContext context = contexts.get(chatId);
        if (context == null) return false;

        if (context.awaitingCancelConfirmation) {
            return handleCancelConfirmation(chatId, context, input);
        }

        boolean processed = onProcessInput(chatId, input, context);
        if (processed) {
            showCancelButton(chatId, getCurrentPrompt(chatId, context));
        }
        return processed;
    }

    @Override
    public final boolean processCallback(Long chatId, String callbackData) {
        DialogContext context = contexts.get(chatId);
        if (context == null) return false;

        if (context.awaitingCancelConfirmation) {
            return handleCancelConfirmationCallback(chatId, context, callbackData);
        }

        if ("DIALOG_CANCEL".equals(callbackData)) {
            return askCancelConfirmation(chatId, context);
        }

        boolean processed = onProcessCallback(chatId, callbackData, context);
        if (processed) {
            showCancelButton(chatId, getCurrentPrompt(chatId, context));
        }
        return processed;
    }

    @Override
    public final boolean isUserInDialog(Long chatId) {
        return contexts.containsKey(chatId);
    }

    @Override
    public final void cancelDialog(Long chatId) {
        DialogContext context = contexts.remove(chatId);
        if (context != null) {
            onCancel(chatId, context);
        }
    }

    // Вспомогательные методы
    private void showCancelButton(Long chatId, String message) {
        if (message == null) return;

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("❌ Отмена")
                .callbackData("DIALOG_CANCEL")
                .build());

        rows.add(row);
        keyboard.setKeyboard(rows);

        sender.sendMessageWithKeyboard(chatId, message, keyboard);
    }

    private boolean askCancelConfirmation(Long chatId, DialogContext context) {
        context.awaitingCancelConfirmation = true;

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("✅ Да, отменить")
                .callbackData("DIALOG_CANCEL_CONFIRM")
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("❌ Нет, продолжить")
                .callbackData("DIALOG_CANCEL_REJECT")
                .build());

        rows.add(row);
        keyboard.setKeyboard(rows);

        sender.sendMessageWithKeyboard(chatId, "❓ Вы уверены, что хотите отменить?", keyboard);
        return true;
    }

    private boolean handleCancelConfirmation(Long chatId, DialogContext context, String input) {
        if (input.equalsIgnoreCase("да") || input.equalsIgnoreCase("yes")) {
            finishCancel(chatId, context);
            return true;
        } else if (input.equalsIgnoreCase("нет") || input.equalsIgnoreCase("no")) {
            context.awaitingCancelConfirmation = false;
            showCancelButton(chatId, getCurrentPrompt(chatId, context));
            return true;
        }

        sender.sendMessage(chatId, "Пожалуйста, ответьте 'да' или 'нет':");
        return true;
    }

    private boolean handleCancelConfirmationCallback(Long chatId, DialogContext context, String callbackData) {
        if ("DIALOG_CANCEL_CONFIRM".equals(callbackData)) {
            finishCancel(chatId, context);
            return true;
        }

        if ("DIALOG_CANCEL_REJECT".equals(callbackData)) {
            context.awaitingCancelConfirmation = false;
            showCancelButton(chatId, getCurrentPrompt(chatId, context));
            return true;
        }

        return false;
    }

    private void finishCancel(Long chatId, DialogContext context) {
        contexts.remove(chatId);
        sender.sendMessage(chatId, "❌ Действие отменено");
        targetHandler.handle(chatId); // Работает для CommandHandler И CallbackHandler
        onCancel(chatId, context);
    }

    protected void finishDialogSuccess(Long chatId, DialogContext context, String successMessage) {
        contexts.remove(chatId);
        sender.sendMessage(chatId, successMessage);
        targetHandler.handle(chatId);
    }

    protected String getCurrentPrompt(Long chatId, DialogContext context) {
        return null; // Переопределить в наследниках при необходимости
    }
}