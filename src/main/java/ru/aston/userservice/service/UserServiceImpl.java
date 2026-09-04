package ru.aston.userservice.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.aston.userservice.dtos.UserCreateDto;
import ru.aston.userservice.dtos.UserResponseDto;
import ru.aston.userservice.dtos.UserUpdateDto;
import ru.aston.userservice.exception.UserNotFoundException;

import ru.aston.userservice.model.User;
import ru.aston.userservice.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        User user = new User(
                userCreateDto.name(),
                userCreateDto.email(),
                userCreateDto.age()
        );

        User savedUser = userRepository.save(user);

        return toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAll() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(userUpdateDto.name());
        user.setEmail(userUpdateDto.email());
        user.setAge(userUpdateDto.age());

        return toResponseDto(user);
    }

    @Override
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);

    }

    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
