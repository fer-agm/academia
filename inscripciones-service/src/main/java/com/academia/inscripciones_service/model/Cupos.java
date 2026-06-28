package com.academia.inscripciones_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cupos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa la disponibilidad de cupos para un curso")
public class Cupos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del registro de cupos (generado automáticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id_cupo;

    @Column(name = "id_curso")
    @NotNull(message = "El curso es obligatorio")
    @Schema(description = "Identificador del curso al que pertenecen los cupos", example = "10")
    private Long idCurso;

    @PositiveOrZero(message = "El número máximo de cupos no puede ser negativo")
    @Schema(description = "Número máximo de cupos disponibles para el curso", example = "30")
    private Integer num_maximo;

    @PositiveOrZero(message = "El número de cupos disponibles no puede ser negativo")
    @Schema(description = "Número de cupos actualmente disponibles", example = "25")
    private Integer num_disponible;
}
