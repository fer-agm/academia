package com.academia.user_service.model;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario (generado automáticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El RUN no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}-[\\dkK]", message = "El RUN debe tener formato válido (ej: 12345678-9)")
    @Schema(description = "RUN del usuario (sin puntos, con guión)", example = "12345678-9")
    private String run;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;

    @Column(unique = true)
    @Schema(description = "Nombre de usuario para iniciar sesión", example = "jperez")
    private String usuario;

    @NotBlank(message = "La clave no puede estar vacía")
    @Schema(description = "Clave de acceso del usuario", example = "1234")
    private String clave;

    @Email(message = "El email debe tener un formato válido")
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@correo.com")
    private String email;

    @Schema(description = "Fecha de registro del usuario (asignada automáticamente)", example = "2026-06-27", accessMode = Schema.AccessMode.READ_ONLY)
    private Date fecha_Registro;
}
