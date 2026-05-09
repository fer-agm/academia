package com.academia.evaluaciones_service.dto;

import com.academia.evaluaciones_service.model.Alternativas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AlternativasDTO {
    private Long idAlternativa;
    private String texto;
    private String correcto;
    private Long idPregunta;

    public Alternativas toModel(){
        return new Alternativas(idAlternativa, texto, correcto, idPregunta);
    }

    public static AlternativasDTO fromModel(Alternativas a){
        if (a == null) return null;
        return new AlternativasDTO(a.getIdAlternativa(), a.getTexto(), a.getCorrecto(), a.getIdPregunta());
    }   

}
