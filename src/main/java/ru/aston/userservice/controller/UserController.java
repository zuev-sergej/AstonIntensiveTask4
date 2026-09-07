package ru.aston.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.aston.userservice.dtos.UserCreateDto;
import ru.aston.userservice.dtos.UserResponseDto;
import ru.aston.userservice.dtos.UserUpdateDto;
import ru.aston.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(
            @Valid @RequestBody UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(
            @PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}")
    public UserResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
