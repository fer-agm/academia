package com.academia.evaluaciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.academia.evaluaciones_service.model.Evaluaciones;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionesDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la evaluación (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id_evaluacion;

    @NotNull(message = "El curso es obligatorio")
    @Schema(description = "Identificador del curso al que pertenece la evaluación", example = "5")
    private Long id_curso;

    @Min(value = 0, message = "El puntaje mínimo no puede ser negativo")
    @Max(value = 100, message = "El puntaje mínimo no puede superar 100")
    @Schema(description = "Puntaje mínimo requerido para aprobar la evaluación", example = "40")
    private int punt_min;

    @Min(value = 1, message = "El puntaje máximo debe ser al menos 1")
    @Max(value = 100, message = "El puntaje máximo no puede superar 100")
    @Schema(description = "Puntaje máximo alcanzable en la evaluación", example = "100")
    private int punt_max;

public Evaluaciones toModel() {
    Evaluaciones e = new Evaluaciones();
    e.setIdCurso(id_curso);
    e.setPuntMin(punt_min);
    e.setPuntMax(punt_max);
    return e;
}

    public static EvaluacionesDTO fromModel(Evaluaciones e) {
        if (e == null) return null;
        return new EvaluacionesDTO(e.getIdEvaluacion(), e.getIdCurso(), e.getPuntMin(), e.getPuntMax());
    }
}