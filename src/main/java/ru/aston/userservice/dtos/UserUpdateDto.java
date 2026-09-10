package ru.aston.userservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Данные для обновления пользователя")
public record UserUpdateDto(

        @Schema(description = "Имя пользователя", example = "Sergey")
        @NotBlank(message = "Имя не может быть пустым")
        String name,

        @Schema(description = "Адрес электронной почты пользователя", example = "sergey@mail.ru")
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат Email")
        String email,

        @Schema(description = "Возраст пользователя", example = "40")
        @Min(value = 0, message = "Неверный возраст")
        Integer age
) {
}
