package com.academia.inscripciones_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO con los datos de un curso provenientes del servicio de clases")
public class CursoDto {
    @Schema(description = "Identificador único del curso", example = "10")
    private Long idCurso;

    @Schema(description = "Nombre del curso", example = "Programación Full Stack")
    private String nombreCurso;

    @Schema(description = "Descripción del curso", example = "Curso introductorio de desarrollo full stack")
    private String descripcionCurso;

    @Schema(description = "Número máximo de cupos del curso", example = "30")
    private Integer maxCupos;

    @Schema(description = "Duración del curso en horas", example = "120")
    private Integer duracionCurso;

    @Schema(description = "Valor del curso en pesos", example = "150000.0")
    private Double valorCurso;
}
