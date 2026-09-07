package ru.aston.userservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDto(
        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат Email")
        String email,

        @Min(value = 0, message = "Неверный возраст")
        Integer age
) {
}
