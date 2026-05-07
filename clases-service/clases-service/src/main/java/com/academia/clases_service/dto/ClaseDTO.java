package com.academia.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ClaseDTO {
    private Long id_clase;
    private String nombre_clase;
    private String contenido_clase;
    private int duracion_clase;
    private Long id_curso;

    public Clase toModel() {
        return new Clase(id_clase, nombre_clase, contenido_clase, duracion_clase, id_curso);
    }

    public static ClaseDTO fromModel(Clase clase) {
        if (clase == null) {
            return null;
        }
        return new ClaseDTO(clase.getId_clase(), clase.getNombre_clase(), clase.getContenido_clase(), clase.getDuracion_clase(), clase.getId_curso());
    }

}
