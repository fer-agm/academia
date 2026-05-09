package com.academia.evaluaciones_service.dto;

import com.academia.evaluaciones_service.model.Evaluaciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EvaluacionesDTO {
    private Long idEvaluacion;
    private Long idCurso;
    private int puntMin;
    private int puntMax;

    public Evaluaciones toModel(){
        return new Evaluaciones(idEvaluacion, idCurso, puntMin, puntMax);
    }       

    public static EvaluacionesDTO fromModel(Evaluaciones e){
        if (e == null) return null;
        return new EvaluacionesDTO(e.getIdEvaluacion(), e.getIdCurso(), e.getPuntMin(), e.getPuntMax());
    }



}
