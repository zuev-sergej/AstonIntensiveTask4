package ru.aston.userservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Данные пользователя")
public record UserResponseDto(
        @Schema(description = "ID пользователя", example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Имя пользователя", example = "Sergey")
        String name,

        @Schema(description = "Адрес электронной почты пользователя", example = "sergey@mail.ru")
        String email,

        @Schema(description = "Возраст пользователя", example = "40")
        Integer age,

        @Schema(description = "Дата и время создания пользователя", example = "2026-09-10T17:00:00",
                accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime createdAt
) {

}
