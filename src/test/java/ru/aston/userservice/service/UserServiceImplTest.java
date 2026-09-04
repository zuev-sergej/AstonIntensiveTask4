package ru.aston.userservice.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.userservice.dtos.UserCreateDto;
import ru.aston.userservice.dtos.UserUpdateDto;
import ru.aston.userservice.exception.UserNotFoundException;
import ru.aston.userservice.kafka.UserEvent;
import ru.aston.userservice.kafka.UserEventProducer;
import ru.aston.userservice.kafka.UserOperation;
import ru.aston.userservice.model.User;
import ru.aston.userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer userEventProducer;

    @BeforeEach
    void setUp() {
        reset(userRepository);
    }

    @Test
    void create_shouldSaveAndReturnDto() {

        var request = new UserCreateDto("Ivan", "ivan@mail.ru", 30);
        var savedUser = new User(1L, "Ivan", "ivan@mail.ru", 30, LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var response = userService.create(request);

        verify(userRepository).save(any(User.class));

        verify(userEventProducer).send(
                new UserEvent(
                        1L,
                        "ivan@mail.ru",
                        UserOperation.CREATED
                )
        );

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Ivan");
        assertThat(response.email()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void getById_shouldReturnDto() {

        Long id = 1L;
        var user = new User(id, "Ivan", "ivan@mail.ru", 30, LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var response = userService.getById(id);

        verify(userRepository).findById(id);
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.name()).isEqualTo("Ivan");
    }

    @Test
    void getById_withNonExistingId_shouldThrowException() {

        Long id = 999999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void getAll_shouldReturnListOfDtos() {

        var user1 = new User(1L, "Ivan", "ivan@mail.ru", 30, LocalDateTime.now());
        var user2 = new User(2L, "Petr", "petr@mail.ru", 25, LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        var responses = userService.getAll();

        verify(userRepository).findAll();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("Ivan");
        assertThat(responses.get(1).name()).isEqualTo("Petr");
    }

    @Test
    void update_shouldUpdateAndReturnDto() {

        Long id = 1L;
        var existingUser = new User(id, "Ivan", "ivan@mail.ru", 30, LocalDateTime.now());
        var request = new UserUpdateDto("Ivan Updated", "ivan.updated@mail.ru", 31);


        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        var response = userService.update(id, request);

        assertThat(existingUser.getName()).isEqualTo("Ivan Updated");
        assertThat(existingUser.getEmail()).isEqualTo("ivan.updated@mail.ru");
        assertThat(existingUser.getAge()).isEqualTo(31);

        assertThat(response.name()).isEqualTo("Ivan Updated");
        assertThat(response.email()).isEqualTo("ivan.updated@mail.ru");
    }

    @Test
    void update_withNonExistingId_shouldThrowException() {

        Long id = 999999L;
        var request = new UserUpdateDto("Ivan Updated", "ivan.updated@mail.ru", 31);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(id, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteUser() {

        Long id = 1L;
        var user = new User(id, "Ivan", "ivan@mail.ru", 30, LocalDateTime.now());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(userRepository).findById(id);

        verify(userEventProducer).send(
                new UserEvent(
                        id,
                        "ivan@mail.ru",
                        UserOperation.DELETED
                )
        );

        verify(userRepository).delete(user);
    }

    @Test
    void delete_withNonExistingId_shouldThrowException() {

        Long id = 999999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(UserNotFoundException.class);
    }
}
