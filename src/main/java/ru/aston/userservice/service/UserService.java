package ru.aston.userservice.service;

import ru.aston.userservice.dtos.UserCreateDto;
import ru.aston.userservice.dtos.UserResponseDto;
import ru.aston.userservice.dtos.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserCreateDto userCreateDto);

    UserResponseDto getById(Long id);

    List<UserResponseDto> getAll();

    UserResponseDto update(Long id, UserUpdateDto userUpdateDto);

    void delete(Long id);
}
