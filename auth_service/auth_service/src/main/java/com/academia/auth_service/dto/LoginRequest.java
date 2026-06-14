package com.academia.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El RUN es obligatorio")
    private String run;

    @NotBlank(message = "La contraseña es obligatoria")
    private String clave;


}
