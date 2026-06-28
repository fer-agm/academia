package com.academia.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciales de acceso")
public class AuthRequest {

    @NotBlank
    @Schema(description = "RUN del usuario (identificador único)", example = "11111111-1")
    private String run;

    @NotBlank
    @Schema(description = "Contraseña del usuario", example = "secreta123")
    private String clave;
}
