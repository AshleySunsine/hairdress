package ru.ash.hairdress.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ash.hairdress.model.Client;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Кастомные методы (Spring Data JPA создаст запросы автоматически)
    Optional<Client> findByEmail(String email);
    List<Client> findByNameContainingIgnoreCase(String name);
}