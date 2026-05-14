package com.academia.inscripciones_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuposDto {
    private Long idCupo;
    private Long idCurso;
    private Integer numMaximo;
    private Integer numDisponible;
}