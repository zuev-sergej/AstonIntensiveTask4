package ru.aston.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.userservice.dtos.UserCreateDto;
import ru.aston.userservice.dtos.UserResponseDto;
import ru.aston.userservice.dtos.UserUpdateDto;
import ru.aston.userservice.service.UserService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(
        name = "Users",
        description = "Операции с пользователями"
)
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создать пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<UserResponseDto>> create(
            @Valid @RequestBody UserCreateDto userCreateDto) {

        UserResponseDto created = userService.create(userCreateDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toEntityModel(created));
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @GetMapping("/{id}")
    public EntityModel<UserResponseDto> getById(
            @PathVariable Long id) {

        UserResponseDto user = userService.getById(id);

        return toEntityModel(user);
    }

    @Operation(summary = "Получить список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользоватей успешно получен")

    @GetMapping
    public CollectionModel<EntityModel<UserResponseDto>> getAll() {

        List<UserResponseDto> users = userService.getAll();

        var userResources = users.stream().map(this::toEntityModel).toList();

        return CollectionModel.of(userResources, linkTo(methodOn(UserController.class)
                .getAll())
                .withSelfRel());
    }

    @Operation(summary = "Обновить пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен")
    @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")

    @PutMapping("/{id}")
    public EntityModel<UserResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {

        UserResponseDto updated = userService.update(id, userUpdateDto);

        return toEntityModel(updated);
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    private EntityModel<UserResponseDto> toEntityModel(UserResponseDto userResponseDto) {
        return EntityModel.of(userResponseDto,
                linkTo(methodOn(UserController.class).getById(userResponseDto.id()))
                        .withSelfRel(),
                linkTo(methodOn(UserController.class).getAll())
                        .withRel("all-users"));
    }
}
