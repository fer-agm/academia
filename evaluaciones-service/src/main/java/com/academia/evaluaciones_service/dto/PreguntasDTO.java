package com.academia.evaluaciones_service.dto;

import com.academia.evaluaciones_service.model.Preguntas;
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
    private Long id_pregunta;

    @NotBlank(message = "El enunciado no puede estar vacío")
    private String enunciado;

    @Min(value = 1, message = "El puntaje debe ser al menos 1")
    private int puntaje;

    @NotNull(message = "El examen es obligatorio")
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