package ru.aston.userservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об ошибке")
public record ErrorResponseDto(

        @Schema(description = "Сообщение об ошибке", example = "пользователь не найден")
        String error
) {

}
