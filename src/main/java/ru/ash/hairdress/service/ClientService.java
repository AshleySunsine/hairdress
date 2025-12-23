package ru.ash.hairdress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ash.hairdress.model.Client;
import ru.ash.hairdress.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    // Получить всех клиентов
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Получить клиента по ID
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    // Создать нового клиента
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    // Обновить клиента
    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));

        client.setName(clientDetails.getName());
        client.setEmail(clientDetails.getEmail());

        return clientRepository.save(client);
    }

    // Удалить клиента
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    // Найти по email
    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    // Найти по имени (частичное совпадение)
    public List<Client> findByName(String name) {
        return clientRepository.findByNameContainingIgnoreCase(name);
    }
}
