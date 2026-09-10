package ru.aston.userservice.repositiry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.aston.userservice.kafka.UserEventProducer;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:15-alpine")
                    .withDatabaseName("userservice_test")
                    .withUsername("test")
                    .withPassword("test");


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserEventProducer userEventProducer;


    @Test
    void userCrud_shouldWorkCorrectly() throws Exception {


        // POST — CREATE USER

        String createRequest = """
                {
                    "name": "Sergey",
                    "email": "sergey@mail.ru",
                    "age": 40
                }
                """;

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Sergey"))
                .andExpect(jsonPath("$.email").value("sergey@mail.ru"))
                .andExpect(jsonPath("$.age").value(40))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();


        Long userId = objectMapper
                .readTree(response)
                .get("id")
                .asLong();


        // GET — READ USER

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Sergey"))
                .andExpect(jsonPath("$.email").value("sergey@mail.ru"))
                .andExpect(jsonPath("$.age").value(40))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists());


        // PUT — UPDATE USER

        String updateRequest = """
                {
                    "name": "Sergey Updated",
                    "email": "sergey.updated@mail.ru",
                    "age": 41
                }
                """;

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Sergey Updated"))
                .andExpect(jsonPath("$.email").value("sergey.updated@mail.ru"))
                .andExpect(jsonPath("$.age").value(41))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists());


        // GET — CHECK UPDATED USER


        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Sergey Updated"))
                .andExpect(jsonPath("$.email").value("sergey.updated@mail.ru"))
                .andExpect(jsonPath("$.age").value(41))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.all-users.href").exists());


        // DELETE — DELETE USER

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());


        // GET — CHECK USER DELETED

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        String invalidRequest = """
                {
                    "name": "Sergey",
                    "email": "invalid-email",
                    "age": 40
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long nonExistingId = 999999L;

        mockMvc.perform(get("/api/users/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long nonExistingId = 999999L;

        String updateRequest = """
                {
                    "name": "Sergey Updated",
                    "email": "sergey.updated@mail.ru",
                    "age": 41
                }
                """;

        mockMvc.perform(put("/api/users/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isNotFound());
    }
}
