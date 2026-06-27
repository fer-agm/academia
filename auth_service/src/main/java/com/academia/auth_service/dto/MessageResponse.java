package com.academia.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mensaje de resultado")
public class MessageResponse {

    @Schema(example = "¡Usuario creado exitosamente!")
    private String message;
}
