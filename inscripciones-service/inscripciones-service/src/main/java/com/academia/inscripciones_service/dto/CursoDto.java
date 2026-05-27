package com.academia.inscripciones_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDto {
    private Long idCurso;
    private String nombreCurso;
    private String descripcionCurso;
    private Integer maxCupos;
    private Integer duracionCurso;
    private Double valorCurso;



    

}