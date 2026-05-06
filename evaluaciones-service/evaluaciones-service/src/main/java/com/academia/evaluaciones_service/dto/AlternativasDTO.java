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
    private String texto;
    private boolean correcto;
    private String id_pregunta;

    public Alternativas toModel(){
        return new Alternativas(texto, correcto, id_pregunta);
    }

    public static AlternativasDTO fromModel(Alternativas a){
        if (a == null) return null;
        return new AlternativasDTO(a.getTexto(), a.isCorrecto(), a.getId_pregunta());
    }   

}
