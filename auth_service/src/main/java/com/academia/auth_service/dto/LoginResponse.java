package com.academia.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado del login")
public class LoginResponse {

    @Schema(description = "ok si las credenciales son válidas, error en caso contrario", example = "ok")
    private String status;

    @Schema(description = "Token JWT (válido 8 horas); vacío si el login falló",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMTExMS0xIn0...")
    private String token;

    @Schema(description = "Mensaje legible del resultado del login", example = "Inicio de sesión exitoso")
    private String mensaje;
}
