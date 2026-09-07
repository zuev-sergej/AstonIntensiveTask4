package ru.aston.userservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.userservice.dtos.UserResponseDto;
import ru.aston.userservice.exception.ExceptionHandler;
import ru.aston.userservice.exception.UserNotFoundException;
import ru.aston.userservice.service.UserService;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ExceptionHandler.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void create_shouldReturnCreateUser() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "Sergey",
                "sergey@mail.ru",
                23,
                LocalDateTime.of(2026, 8, 29, 20, 30)
        );

        when(userService.create(any())).thenReturn(responseDto);

        String requestJson = """
                {
                  "name": "Sergey",
                  "email": "sergey@mail.ru",
                  "age": 23
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sergey"))
                .andExpect(jsonPath("$.email").value("sergey@mail.ru"))
                .andExpect(jsonPath("$.age").value(23));

        verify(userService).create(any());
    }

    @Test
    void getById_shouldReturnUser() throws Exception {

        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "Sergey",
                "sergey@mail.ru",
                40,
                LocalDateTime.of(2026, 8, 29, 15, 0)
        );

        when(userService.getById(1L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sergey"))
                .andExpect(jsonPath("$.email")
                        .value("sergey@mail.ru"))
                .andExpect(jsonPath("$.age").value(40));

        verify(userService).getById(1L);
    }

    @Test
    void getAll_shouldReturnUsers() throws Exception {

        UserResponseDto firstUser = new UserResponseDto(
                1L,
                "Sergey",
                "sergey@mail.ru",
                40,
                LocalDateTime.of(2026, 8, 29, 15, 0)
        );

        UserResponseDto secondUser = new UserResponseDto(
                2L,
                "Alex",
                "alex@mail.ru",
                30,
                LocalDateTime.of(2026, 8, 29, 15, 10)
        );

        when(userService.getAll())
                .thenReturn(List.of(firstUser, secondUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Sergey"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Alex"));

        verify(userService).getAll();
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {

        UserResponseDto responseDto = new UserResponseDto(
                1L,
                "Sergey Updated",
                "sergey.updated@mail.ru",
                41,
                LocalDateTime.of(2026, 8, 29, 15, 0)
        );

        when(userService.update(eq(1L), any()))
                .thenReturn(responseDto);

        String requestJson = """
                {
                    "name": "Sergey Updated",
                    "email": "sergey.updated@mail.ru",
                    "age": 41
                }
                """;

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Sergey Updated"))
                .andExpect(jsonPath("$.email")
                        .value("sergey.updated@mail.ru"))
                .andExpect(jsonPath("$.age").value(41));

        verify(userService)
                .update(eq(1L), any());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {

        doNothing()
                .when(userService)
                .delete(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    void getById_whenUserDoesNotExist_shouldReturnNotFound()
            throws Exception {

        when(userService.getById(999L))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Пользователь с id: 999 не найден"));

        verify(userService).getById(999L);
    }

    @Test
    void create_whenInvalidRequest_shouldReturnBadRequest()
            throws Exception {

        String requestJson = """
                {
                    "name": "",
                    "email": "invalid-email",
                    "age": -10
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verifyNoInteractions(userService);
    }
}
