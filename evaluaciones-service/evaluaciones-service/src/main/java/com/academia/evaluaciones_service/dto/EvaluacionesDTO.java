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
    private String id_evaluacion;
    private String id_curso;
    private int punt_min;
    private int punt_max;

    public Evaluaciones toModel(){
        return new Evaluaciones(id_evaluacion, id_curso, punt_min, punt_max);
    }       

    public static EvaluacionesDTO fromModel(Evaluaciones e){
        if (e == null) return null;
        return new EvaluacionesDTO(e.getId_evaluacion(), e.getId_curso(), e.getPunt_min(), e.getPunt_max());
    }



}
