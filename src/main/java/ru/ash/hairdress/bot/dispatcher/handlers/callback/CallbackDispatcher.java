package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.dispatcher.service.dialog.DialogRegistry;
import ru.ash.hairdress.bot.dispatcher.service.dialog.DialogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class CallbackDispatcher {
    private Map<String, CallbackHandler> handlers = new HashMap<>();
    private final DialogRegistry dialogRegistry;

    public CallbackDispatcher(List<CallbackHandler> handlerList,
                              DialogRegistry dialogRegistry) {
        this.dialogRegistry = dialogRegistry;
        this.handlers = new HashMap<>();

        for (CallbackHandler handler : handlerList) {
            handlers.put(handler.getCallback(), handler);
        }

        log.info("Зарегистрировано callback-ов: {}", handlers.keySet());
    }

    public void dispatch(String callbackData, Long chatId) {
        log.info("Dispatching command: {}", callbackData);
        // 1. Проверяем активный диалог
        Optional<DialogService> activeDialog = dialogRegistry.findActiveDialog(chatId);
        if (activeDialog.isPresent()) {
            boolean processed = activeDialog.get().processCallback(chatId, callbackData);
            if (processed) return; // Диалог обработал свою кнопку
        }

        // 2. Обычный callback
        CallbackHandler handler = handlers.getOrDefault(callbackData,
                handlers.get("UNKNOWN"));
        handler.handle(chatId);
    }

    private void printAllCallback() {
        StringBuilder response = new StringBuilder("📋 Зарегистрированные кол-бэки:\n");
        handlers.forEach((callback, handler) -> {
            response.append("\n• ")
                    .append(callback)
                    .append(" → ")
                    .append(handler.getClass().getSimpleName());
        });
        log.info(response.toString());
    }
}