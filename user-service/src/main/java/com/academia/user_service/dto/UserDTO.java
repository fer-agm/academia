package com.academia.user_service.dto;

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
    private String id;

    @NotBlank(message = "El RUN no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}-[\\dkK]", message = "El RUN debe tener formato válido (ej: 12345678-9)")
    private String run;

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    private String apellido;

    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La clave no puede estar vacía")
    @Size(min = 4, message = "La clave debe tener al menos 4 caracteres")
    private String clave;



}