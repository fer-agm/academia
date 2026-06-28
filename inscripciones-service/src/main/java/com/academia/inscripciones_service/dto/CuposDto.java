package com.academia.inscripciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la transferencia de datos de cupos")
public class CuposDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del registro de cupos (generado automáticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idCupo;

    @NotNull(message = "El curso es obligatorio")
    @Schema(description = "Identificador del curso al que pertenecen los cupos", example = "10")
    private Long idCurso;

    @NotNull(message = "El número máximo de cupos es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 cupo")
    @Schema(description = "Número máximo de cupos disponibles para el curso", example = "30")
    private Integer numMaximo;

    @NotNull(message = "El número de cupos disponibles es obligatorio")
    @Min(value = 0, message = "Los cupos disponibles no pueden ser negativos")
    @Schema(description = "Número de cupos actualmente disponibles", example = "25")
    private Integer numDisponible;
}
