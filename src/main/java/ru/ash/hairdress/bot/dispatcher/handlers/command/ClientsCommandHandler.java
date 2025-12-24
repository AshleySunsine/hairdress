package ru.ash.hairdress.bot.dispatcher.handlers.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ash.hairdress.bot.sender.MessageSender;
import ru.ash.hairdress.model.Client;
import ru.ash.hairdress.service.ClientService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientsCommandHandler implements CommandHandler {
    private final ClientService clientService;
    private final MessageSender sender;

    @Override
    public String getCommand() {
        return "/clients";
    }

    @Override
    public void handle(Long chatId, boolean override) {
        List<Client> clients = clientService.getAllClients();

        if (clients.isEmpty()) {
            sender.sendMessage(chatId, "📭 В базе данных нет пользователей.", null, override);
            return;
        }

        StringBuilder response = new StringBuilder("📋 Список клиентов:\n\n");
        for (int i = 0; i < clients.size(); i++) {
            Client client = clients.get(i);
            response.append(i + 1)
                    .append(". ")
                    .append(client.getName())
                    .append(" (ID: ")
                    .append(client.getId())
                    .append(")\n");
        }

        response.append("\nВсего клиентов: ").append(clients.size());
        sender.sendMessage(chatId, response.toString(), null, true);
    }
}