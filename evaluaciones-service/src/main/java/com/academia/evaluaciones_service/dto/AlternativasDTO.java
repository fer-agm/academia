package com.academia.evaluaciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.academia.evaluaciones_service.model.Alternativas;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class AlternativasDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la alternativa (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id_alternativa;

    @NotBlank(message = "El texto no puede estar vacío")
    @Schema(description = "Texto de la alternativa de respuesta", example = "París")
    private String texto;

    @NotNull(message = "Debe indicar si es correcta o no")
    @Schema(description = "Indica si la alternativa es la respuesta correcta", example = "true")
    private Boolean correcto;  // ← Boolean con mayúscula

    @NotNull(message = "La pregunta es obligatoria")
    @Schema(description = "Identificador de la pregunta a la que pertenece la alternativa", example = "20")
    private Long id_pregunta;

    public Alternativas toModel() {
        Alternativas a = new Alternativas();
        a.setTexto(texto);
        a.setCorrecto(correcto);
        a.setIdPregunta(id_pregunta);
        return a;
    }

    public static AlternativasDTO fromModel(Alternativas a) {
        if (a == null) return null;
        return new AlternativasDTO(a.getIdAlternativa(), a.getTexto(), a.getCorrecto(), a.getIdPregunta());
    }
}