package ru.ash.hairdress.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ash.hairdress.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Кастомные методы (Spring Data JPA создаст запросы автоматически)
    Optional<User> findByEmail(String email);
    List<User> findByNameContainingIgnoreCase(String name);
}