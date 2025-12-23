package ru.ash.hairdress.bot.dispatcher.handlers.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CallbackDispatcher {
    private Map<String, CallbackHandler> handlers = new HashMap<>();

    public CallbackDispatcher(List<CallbackHandler> handlerList) {
        for (CallbackHandler handler : handlerList) {
            handlers.put(handler.getCallback(), handler);
        }
    }

    public void dispatch(String callbackData, Long chatId) {
        log.info("Dispatching command: {}", callbackData);
        printAllCallback();
        CallbackHandler handler = handlers.getOrDefault(callbackData, handlers.get("UNKNOWN"));
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