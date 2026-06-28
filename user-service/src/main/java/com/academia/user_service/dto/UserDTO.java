package com.academia.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del usuario (generado automáticamente)", example = "1")
    private Long id;

    @NotBlank(message = "El RUN no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}-[\\dkK]", message = "El RUN debe tener formato válido (ej: 12345678-9)")
    @Schema(description = "RUN del usuario (sin puntos, con guión)", example = "12345678-9")
    private String run;

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;

    @Email(message = "El email debe tener un formato válido")
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@correo.com")
    private String email;

    @NotBlank(message = "La clave no puede estar vacía")
    @Size(min = 4, message = "La clave debe tener al menos 4 caracteres")
    @Schema(description = "Clave de acceso del usuario", example = "1234")
    private String clave;



}
