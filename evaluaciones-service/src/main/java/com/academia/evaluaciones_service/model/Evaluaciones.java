package com.academia.evaluaciones_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Evaluaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la evaluación (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idEvaluacion;

    @NotNull(message = "El curso es obligatorio")
    @Schema(description = "Identificador del curso al que pertenece la evaluación", example = "5")
    private Long idCurso;

    @Min(value = 0, message = "El puntaje mínimo no puede ser negativo")
    @Max(value = 100, message = "El puntaje mínimo no puede superar 100")
    @Schema(description = "Puntaje mínimo requerido para aprobar la evaluación", example = "40")
    private int puntMin;

    @Min(value = 1, message = "El puntaje máximo debe ser al menos 1")
    @Max(value = 100, message = "El puntaje máximo no puede superar 100")
    @Schema(description = "Puntaje máximo alcanzable en la evaluación", example = "100")
    private int puntMax;


}
