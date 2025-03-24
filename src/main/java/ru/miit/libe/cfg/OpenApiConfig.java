package ru.miit.libe.cfg;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(
                        Arrays.asList(
                                // туннель
//                                new Server().url("https://bitoche.cloudpub.ru"),
                                // локальный хост
                                new Server().url("http://localhost:8080")
                        )
                );
    }
}
