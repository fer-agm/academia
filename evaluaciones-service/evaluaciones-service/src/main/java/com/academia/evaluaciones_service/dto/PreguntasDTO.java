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
    private Long idPregunta;
    private String enunciado;
    private int puntaje;
    private Long idEvaluacion;

    public Preguntas toModel(){
        return new Preguntas(idPregunta, enunciado, puntaje, idEvaluacion);
    }   

    public static PreguntasDTO fromModel(Preguntas p){
        if (p == null) return null;
        return new PreguntasDTO(p.getIdPregunta(), p.getEnunciado(), p.getPuntaje(), p.getIdEvaluacion());
    }   

}
