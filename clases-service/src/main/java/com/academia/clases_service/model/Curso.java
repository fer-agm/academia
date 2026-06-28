package com.academia.clases_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del curso, generado automáticamente", example = "1")
    private Long idCurso;

    @NotBlank(message = "El nombre del curso no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    @Schema(description = "Nombre del curso", example = "Java Básico")
    private String nombreCurso;

    @PositiveOrZero(message = "La duración no puede ser negativa")
    @Schema(description = "Duración del curso en horas", example = "40")
    private int duracionCurso;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Schema(description = "Descripción del curso", example = "Curso introductorio de programación en Java")
    private String descripcionCurso;

    @PositiveOrZero(message = "El valor no puede ser negativo")
    @Schema(description = "Valor del curso", example = "100000.0")
    private double valorCurso;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "Identificador de la categoría a la que pertenece el curso", example = "1")
    private Long idCategoria;

    @PositiveOrZero(message = "El número de cupos no puede ser negativo")
    @Schema(description = "Cantidad máxima de cupos disponibles", example = "30")
    private int maxCupos;
    //private Long idInstructor;




}
