package com.academia.evaluaciones_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Preguntas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la pregunta (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idPregunta;

    @NotBlank(message = "El enunciado no puede estar vacío")
    @Schema(description = "Enunciado o texto de la pregunta", example = "¿Cuál es la capital de Francia?")
    private String enunciado;

    @Min(value = 1, message = "El puntaje debe ser al menos 1")
    @Schema(description = "Puntaje asignado a la pregunta", example = "5")
    private int puntaje;

    @NotNull(message = "La evaluación asociada es obligatoria")
    @Schema(description = "Identificador de la evaluación a la que pertenece la pregunta", example = "10")
    private Long idEvaluacion;


}
