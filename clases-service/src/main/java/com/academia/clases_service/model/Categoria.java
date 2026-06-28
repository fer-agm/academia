package com.academia.clases_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la categoría, generado automáticamente", example = "1")
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Schema(description = "Nombre de la categoría", example = "Programación")
    private String nombreCategoria;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    @Schema(description = "Descripción de la categoría", example = "Cursos de programación y desarrollo de software")
    private String descripcionCategoria;

}


