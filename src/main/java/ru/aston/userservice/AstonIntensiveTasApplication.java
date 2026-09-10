package ru.aston.userservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@OpenAPIDefinition(
        info = @Info(
                title = "User service",
                description = "API для упраления данными пользователя"
        )
)
@SpringBootApplication
public class AstonIntensiveTasApplication {

    public static void main(String[] args) {
        SpringApplication.run(AstonIntensiveTasApplication.class, args);
    }

}
