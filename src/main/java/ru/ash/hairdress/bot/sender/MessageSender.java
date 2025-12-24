package ru.ash.hairdress.bot.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MessageSender {
    private final TelegramLongPollingBot bot;
    private final Map<Long, DialogMessageState> dialogStates = new ConcurrentHashMap<>();

    private static class DialogMessageState {
        Integer dialogMessageId; // ID сообщения диалога
        String currentText;
        InlineKeyboardMarkup currentKeyboard;
    }

    @Autowired
    public MessageSender(@Lazy TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    // ==================== ДИАЛОГОВЫЕ СООБЩЕНИЯ ====================

    /**
     * Начать диалог - отправляет новое сообщение и запоминает его
     */
    private Integer overrideDialogMessage(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        try {
            // Удаляем предыдущее диалоговое сообщение, если есть
            cleanupDialogMessage(chatId);

            // Отправляем новое
            SendMessage message = new SendMessage(chatId.toString(), text);
            if (keyboard != null) {
                message.setReplyMarkup(keyboard);
            }

            Message sentMessage = bot.execute(message);
            Integer messageId = sentMessage.getMessageId();

            // Сохраняем состояние
            DialogMessageState state = new DialogMessageState();
            state.dialogMessageId = messageId;
            state.currentText = text;
            state.currentKeyboard = keyboard;
            dialogStates.put(chatId, state);

            return messageId;
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки диалогового сообщения", e);
            return null;
        }
    }

    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard, boolean override) {
        if (override) {
            overrideDialogMessage(chatId, text, keyboard);
        } else {
            sendMessage(chatId, text, keyboard);
        }
    }


    private void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        try {
            SendMessage message = new SendMessage(chatId.toString(), text);
            if (keyboard != null) {
                message.setReplyMarkup(keyboard);
            }
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    // ==================== УТИЛИТЫ ====================

    /**
     * Отправить временное сообщение (исчезнет через время)
     */
    public void sendTemporaryMessage(Long chatId, String text, int secondsToLive) {
        try {
            SendMessage message = new SendMessage(chatId.toString(), text);
            Message sent = bot.execute(message);

            // Удалить через N секунд
            scheduleDelete(chatId, sent.getMessageId(), secondsToLive);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки временного сообщения", e);
        }
    }

    private void scheduleDelete(Long chatId, Integer messageId, int delaySeconds) {
        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds * 1000L);
                DeleteMessage delete = new DeleteMessage(chatId.toString(), messageId);
                bot.execute(delete);
            } catch (Exception e) {
                log.warn("Не удалось удалить временное сообщение", e);
            }
        }).start();
    }

    /**
     * Обновить диалоговое сообщение (редактирование)
     */
    public boolean updateDialogMessage(Long chatId, String newText, InlineKeyboardMarkup newKeyboard) {
        DialogMessageState state = dialogStates.get(chatId);
        if (state == null || state.dialogMessageId == null) {
            // Нет диалогового сообщения - создаём новое
            overrideDialogMessage(chatId, newText, newKeyboard);
            return true;
        }

        try {
            // Редактируем текст
            EditMessageText editText = new EditMessageText();
            editText.setChatId(chatId.toString());
            editText.setMessageId(state.dialogMessageId);
            editText.setText(newText);
            editText.setParseMode("Markdown");
            bot.execute(editText);

            // Редактируем клавиатуру
            EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
            editMarkup.setChatId(chatId.toString());
            editMarkup.setMessageId(state.dialogMessageId);
            editMarkup.setReplyMarkup(newKeyboard);
            bot.execute(editMarkup);

            // Обновляем состояние
            state.currentText = newText;
            state.currentKeyboard = newKeyboard;

            return true;
        } catch (TelegramApiException e) {
            log.error("Ошибка обновления диалогового сообщения", e);
            return false;
        }
    }

    /**
     * Завершить диалог - удалить диалоговое сообщение
     */
    public void finishDialogMessage(Long chatId) {
        cleanupDialogMessage(chatId);
        dialogStates.remove(chatId);
    }

    /**
     * Получить текущий текст диалогового сообщения
     */
    public String getCurrentDialogText(Long chatId) {
        DialogMessageState state = dialogStates.get(chatId);
        return state != null ? state.currentText : null;
    }

    private void cleanupDialogMessage(Long chatId) {
        DialogMessageState oldState = dialogStates.get(chatId);
        if (oldState != null && oldState.dialogMessageId != null) {
            try {
                DeleteMessage delete = new DeleteMessage(chatId.toString(), oldState.dialogMessageId);
                bot.execute(delete);
            } catch (TelegramApiException e) {
                // Сообщение могло быть уже удалено - это нормально
            }
        }
    }
}