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
    private Long id_alternativa;
    private String texto;
    private String correcto;
    private Long id_pregunta;

    public Alternativas toModel(){
        return new Alternativas(id_alternativa, texto, correcto, id_pregunta);
    }

    public static AlternativasDTO fromModel(Alternativas a){
        if (a == null) return null;
        return new AlternativasDTO(a.getId_alternativa(), a.getTexto(), a.getCorrecto(), a.getId_pregunta());
    }   

}
