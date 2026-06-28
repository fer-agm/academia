package com.academia.evaluaciones_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Alternativas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la alternativa (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idAlternativa;

    @NotBlank(message = "El texto de la alternativa no puede estar vacío")
    @Schema(description = "Texto de la alternativa de respuesta", example = "París")
    private String texto;

    @NotNull(message = "Debe indicar si la alternativa es correcta o no")
    @Schema(description = "Indica si la alternativa es la respuesta correcta", example = "true")
    private Boolean correcto;

    @NotNull(message = "La pregunta asociada es obligatoria")
    @Schema(description = "Identificador de la pregunta a la que pertenece la alternativa", example = "20")
    private Long idPregunta;


}
