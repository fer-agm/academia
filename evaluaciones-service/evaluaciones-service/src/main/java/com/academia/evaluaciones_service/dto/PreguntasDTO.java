package com.academia.evaluaciones_service.dto;

import com.academia.evaluaciones_service.model.Preguntas;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PreguntasDTO {
    private Long id_pregunta;
    private String enunciado;
    private int puntaje;
    private Long id_evaluacion;

    public Preguntas toModel(){
        return new Preguntas(id_pregunta, enunciado, puntaje, id_evaluacion);
    }   

    public static PreguntasDTO fromModel(Preguntas p){
        if (p == null) return null;
        return new PreguntasDTO(p.getId_pregunta(), p.getEnunciado(), p.getPuntaje(), p.getId_evaluacion());
    }   

}
