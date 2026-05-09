package com.academia.clases_service.dto;

import com.academia.clases_service.model.Curso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoDTO {
    private Long idCurso;
    private String nombreCurso;
    private int duracionCurso;
    private String descripcionCurso;
    private double valorCurso;
    private Long idCategoria;
    private int maxCupos;

    public Curso toModel() {
        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        curso.setNombreCurso(nombreCurso);
        curso.setDuracionCurso(duracionCurso);
        curso.setDescripcionCurso(descripcionCurso);
        curso.setValorCurso(valorCurso);
        curso.setIdCategoria(idCategoria);
        curso.setMaxCupos(maxCupos);
        return curso;
    }

    public static CursoDTO fromModel(Curso curso) {
        if (curso == null) {
            return null;
        }
        return new CursoDTO(curso.getIdCurso(), curso.getNombreCurso(), curso.getDuracionCurso(), curso.getDescripcionCurso(), curso.getValorCurso(), curso.getIdCategoria(), curso.getMaxCupos());
    }

}
