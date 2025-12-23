package ru.ash.hairdress.bot.dispatcher.handlers.command;

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
public class CommandDispatcher {
    private final Map<String, CommandHandler> handlers;
    private final DialogRegistry dialogRegistry;

    public CommandDispatcher(List<CommandHandler> handlerList,
                             DialogRegistry dialogRegistry) {
        this.dialogRegistry = dialogRegistry;
        this.handlers = new HashMap<>();

        for (CommandHandler handler : handlerList) {
            handlers.put(handler.getCommand(), handler);
        }

        log.info("Зарегистрировано команд: {}", handlers.keySet());
    }

    public void dispatch(String messageText, Long chatId) {
        Optional<DialogService> activeDialog = dialogRegistry.findActiveDialog(chatId);

        if (activeDialog.isPresent()) {
            DialogService dialog = activeDialog.get();
            boolean processed = dialog.processInput(chatId, messageText);

            if (!processed) {
                // Если ввод не обработан - сбрасываем все диалоги
                dialogRegistry.cancelAllDialogs(chatId);
                handleCommand(messageText, chatId);
            }
            return;
        }

        // 2. Нет активного диалога - обычная команда
        handleCommand(messageText, chatId);
    }

    private void handleCommand(String messageText, Long chatId) {
        log.info("Dispatching command: {}", messageText);

        CommandHandler handler = handlers.getOrDefault(messageText,
                handlers.get("/unknown"));
        handler.handle(chatId);
    }

    private void printAllCommands() {
        StringBuilder response = new StringBuilder("📋 Зарегистрированные обработчики:\n");
        handlers.forEach((command, handler) -> {
            response.append("\n• ")
                    .append(command)
                    .append(" → ")
                    .append(handler.getClass().getSimpleName());
        });
        log.info(response.toString());
    }
}
