package com.academia.clases_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.academia.clases_service.model.Curso;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del curso, generado automáticamente", example = "1")
    private Long idCurso;

    @NotBlank(message = "El nombre del curso no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    @Schema(description = "Nombre del curso", example = "Java Básico")
    private String nombreCurso;

    @Positive(message = "La duración debe ser mayor a 0")
    @Schema(description = "Duración del curso en horas", example = "40")
    private int duracionCurso;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Schema(description = "Descripción del curso", example = "Curso introductorio de programación en Java")
    private String descripcionCurso;

    @Positive(message = "El valor debe ser mayor a 0")
    @Schema(description = "Valor del curso", example = "100000.0")
    private double valorCurso;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "Identificador de la categoría a la que pertenece el curso", example = "1")
    private Long idCategoria;

    @Min(value = 1, message = "Debe haber al menos 1 cupo")
    @Schema(description = "Cantidad máxima de cupos disponibles", example = "30")
    private int maxCupos;

    public Curso toModel() {
        Curso curso = new Curso();
        curso.setNombreCurso(nombreCurso);
        curso.setDuracionCurso(duracionCurso);
        curso.setDescripcionCurso(descripcionCurso);
        curso.setValorCurso(valorCurso);
        curso.setIdCategoria(idCategoria);
        curso.setMaxCupos(maxCupos);
        return curso;
    }

    public static CursoDTO fromModel(Curso c) {
        if (c == null) return null;
        return new CursoDTO(c.getIdCurso(), c.getNombreCurso(), c.getDuracionCurso(), c.getDescripcionCurso(), c.getValorCurso(), c.getIdCategoria(), c.getMaxCupos());
    }
}