package ru.aston.userservice.kafka;

public record UserEvent(
        Long id,
        String email,
        UserOperation operation
) {
}
