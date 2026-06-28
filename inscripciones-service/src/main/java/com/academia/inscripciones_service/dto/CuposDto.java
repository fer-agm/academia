package com.academia.inscripciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuposDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idCupo;

    @NotNull(message = "El curso es obligatorio")
    private Long idCurso;

    @NotNull(message = "El número máximo de cupos es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 cupo")
    private Integer numMaximo;

    @NotNull(message = "El número de cupos disponibles es obligatorio")
    @Min(value = 0, message = "Los cupos disponibles no pueden ser negativos")
    private Integer numDisponible;
}