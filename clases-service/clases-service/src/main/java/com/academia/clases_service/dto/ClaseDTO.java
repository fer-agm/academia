package com.academia.clases_service.dto;

import com.academia.clases_service.model.Clase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClaseDTO {
    private Long idClase;
    private Long realizada;
    private Long idCurso;

    public Clase toModel() {
        Clase clase = new Clase();
        clase.setIdClase(idClase);
        clase.setRealizada(realizada);
        clase.setIdCurso(idCurso);
        return clase;
    }

    public static ClaseDTO fromModel(Clase clase) {
        if (clase == null) {
            return null;
        }
        return new ClaseDTO(clase.getIdClase(), clase.getRealizada(), clase.getIdCurso());
    }

}
