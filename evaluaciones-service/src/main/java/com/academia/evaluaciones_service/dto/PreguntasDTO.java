package com.academia.evaluaciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.academia.evaluaciones_service.model.Preguntas;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreguntasDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la pregunta (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id_pregunta;

    @NotBlank(message = "El enunciado no puede estar vacío")
    @Schema(description = "Enunciado o texto de la pregunta", example = "¿Cuál es la capital de Francia?")
    private String enunciado;

    @Min(value = 1, message = "El puntaje debe ser al menos 1")
    @Schema(description = "Puntaje asignado a la pregunta", example = "5")
    private int puntaje;

    @NotNull(message = "El examen es obligatorio")
    @Schema(description = "Identificador de la evaluación (examen) a la que pertenece la pregunta", example = "10")
    private Long id_examen;

    public Preguntas toModel() {
        Preguntas p = new Preguntas();
        p.setEnunciado(enunciado);
        p.setPuntaje(puntaje);
        p.setIdEvaluacion(id_examen);
        return p;
    }

    public static PreguntasDTO fromModel(Preguntas p) {
        if (p == null) return null;
        return new PreguntasDTO(p.getIdPregunta(), p.getEnunciado(), p.getPuntaje(), p.getIdEvaluacion());
    }
}