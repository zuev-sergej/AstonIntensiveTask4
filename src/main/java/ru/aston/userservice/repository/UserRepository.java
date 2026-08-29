package ru.aston.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.userservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
