package ru.ash.hairdress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ash.hairdress.model.Client;
import ru.ash.hairdress.service.ClientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class UserController {

    @Autowired
    private ClientService clientService;

    // GET: Получить всех пользователей
    @GetMapping
    public ResponseEntity<List<Client>> getAllUsers() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    // GET: Получить пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<Client> getUserById(@PathVariable Long id) {
        Optional<Client> user = clientService.getClientById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Создать нового пользователя
    @PostMapping
    public ResponseEntity<Client> createUser(@RequestBody Client client) {
        Client createdClient = clientService.createClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }

    // PUT: Обновить пользователя
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateUser(@PathVariable Long id, @RequestBody Client client) {
        try {
            Client updatedClient = clientService.updateClient(id, client);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: Удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    // GET: Поиск по email
    @GetMapping("/search/email")
    public ResponseEntity<Client> findByEmail(@RequestParam String email) {
        Optional<Client> user = clientService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET: Поиск по имени
    @GetMapping("/search/name")
    public ResponseEntity<List<Client>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(clientService.findByName(name));
    }
}
