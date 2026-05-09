package com.academia.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.academia.clases_service.model.Clase;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClaseDTO {
    private Long id_clase;
    private Long realizada;
    private Long id_curso;

    public Clase toModel() {
        Clase clase = new Clase();
        clase.setIdClase(id_clase);
        clase.setRealizada(realizada);
        clase.setIdCurso(id_curso);
        return clase;
    }

    public static ClaseDTO fromModel(Clase clase) {
        if (clase == null) {
            return null;
        }
        return new ClaseDTO(clase.getIdClase(), clase.getRealizada(), clase.getIdCurso());
    }

}
