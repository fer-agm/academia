package com.academia.calificaciones_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
@Configuration
public class SwaggerConfig {

@Bean
public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";


return new OpenAPI()
        .info(new Info()
                .title("API 2026 Registro de calificaciones")
                .version("1.0")
                .description("Documentación de la API para el sistema de academia"))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        // 2. Define cómo se compone el esquema (Un Header de tipo HTTP Bearer con formato JWT)
        .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
}
}