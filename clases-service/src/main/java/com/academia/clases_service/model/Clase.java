package com.academia.clases_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la clase, generado automáticamente", example = "1")
    private Long idClase;

    @Schema(description = "Nombre de la clase", example = "Introducción a las variables")
    private String nombreClase;

    @Schema(description = "Contenido o temario de la clase", example = "Tipos de datos, declaración y asignación de variables")
    private String contenidoClase;

    @Schema(description = "Duración de la clase en minutos", example = "90")
    private int duracionClase;

    @Schema(description = "Indica si la clase ya fue realizada (0 = no, 1 = sí)", example = "0")
    private Long realizada;

    @Schema(description = "Identificador del curso al que pertenece la clase", example = "1")
    private Long idCurso;

}


