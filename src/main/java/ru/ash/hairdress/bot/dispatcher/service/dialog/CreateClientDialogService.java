package ru.ash.hairdress.bot.dispatcher.service.dialog;

import org.springframework.stereotype.Service;
import ru.ash.hairdress.bot.dispatcher.handlers.callback.ClientManagementCallbackHandler;
import ru.ash.hairdress.bot.sender.MessageSender;
import ru.ash.hairdress.model.Client;
import ru.ash.hairdress.service.ClientService;

@Service
public class CreateClientDialogService extends BaseDialogService {
    private final ClientService clientService;

    public CreateClientDialogService(
            MessageSender sender,
            ClientManagementCallbackHandler clientManagementHandler,
            ClientService clientService) {
        super(sender, clientManagementHandler); // Возврат в меню управления клиентами
        this.clientService = clientService;
    }

    @Override
    protected void onStart(Long chatId, DialogContext context) {
        context.put("step", "name");
    }

    @Override
    protected boolean onProcessInput(Long chatId, String input, DialogContext context) {
        String step = context.get("step");

        if ("name".equals(step)) {
            context.put("name", input);
            context.put("step", "phone");
            return true;
        } else if ("phone".equals(step)) {
            context.put("phone", input);
            context.put("step", "optional_email");
            return true;
        } else if ("optional_email".equals(step)) {
            if ("/skip".equalsIgnoreCase(input)) {
                context.put("email", null);
            } else {
                context.put("email", input);
            }
            finishDialog(chatId, context);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onProcessCallback(Long chatId, String callbackData, DialogContext context) {
        if ("DIALOG_SKIP_EMAIL".equals(callbackData)) {
            context.put("email", null);
            finishDialog(chatId, context);
            return true;
        }
        return false;
    }

    @Override
    protected String getCurrentPrompt(Long chatId, DialogContext context) {
        String step = context.get("step");
        switch (step) {
            case "name": return "Введите имя клиента:";
            case "phone": return "Введите телефон клиента:";
            case "optional_email": return "Введите email (или /skip чтобы пропустить):";
            default: return null;
        }
    }

    private void finishDialog(Long chatId, DialogContext context) {
        Client client = new Client(); // Бывший User
        client.setName(context.get("name"));
        client.setPhone(context.get("phone"));
        client.setEmail(context.get("email"));

        clientService.createClient(client);

        finishDialogSuccess(chatId, context,
                "✅ Клиент " + client.getName() + " создан!");
    }

    private void sendMessage(Long chatId, String text) {
        // Отправка без кнопки отмены (кнопка добавится в showCancelButton)
        sender.sendMessage(chatId, text);
    }

    @Override
    protected void onCancel(Long chatId, DialogContext context) {
        // Очистка ресурсов (если нужна)
    }
}
