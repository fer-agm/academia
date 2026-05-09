package com.academia.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoDTO {
    private Long id_curso;
    private String nombre_curso;
    private int duracion_curso;
    private String descripcion_curso;
    private double valor_curso;
    private Long id_categoria;
    private int max_cupos;

    public Curso toModel() {
        return new Curso(id_curso, nombre_curso, duracion_curso, descripcion_curso, valor_curso, id_categoria, max_cupos);
    }

    public static CursoDTO fromModel(Curso curso) {
        if (curso == null) {
            return null;
        }
        return new CursoDTO(curso.getId_curso(), curso.getNombre_curso(), curso.getDuracion_curso(), curso.getDescripcion_curso(), curso.getValor_curso(), curso.getId_categoria(), curso.getMax_cupos());
    }

}
