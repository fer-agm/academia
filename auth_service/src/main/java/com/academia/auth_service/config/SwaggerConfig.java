package com.academia.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Relative server -> Swagger UI calls through its own origin (the gateway),
                // not the internal Docker hostname.
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("API 2026 Autenticación")
                        .version("1.0")
                        .description("Registro y login de usuarios"));
    }
}
