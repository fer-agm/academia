package com.academia.calificaciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa el promedio general de un estudiante en un curso")
public class PromedioDTO extends RepresentationModel<PromedioDTO> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del promedio (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idPromedio;

    @NotNull(message = "El ID del curso es obligatorio")
    @Schema(description = "Identificador del curso asociado al promedio", example = "101")
    private Long idCurso;

    @NotBlank(message = "El ID del estudiante es obligatorio")
    @Schema(description = "Identificador del estudiante asociado al promedio", example = "EST-1024")
    private String idEstudiante;

    @Min(value = 1, message = "El promedio mínimo es 1.0")
    @Max(value = 7, message = "El promedio máximo es 7.0")
    @Schema(description = "Promedio general del estudiante en el curso (escala de 1.0 a 7.0)", example = "6.2")
    private Double promedioGeneral;

    @PositiveOrZero(message = "El total de evaluaciones no puede ser negativo")
    @Schema(description = "Cantidad total de evaluaciones consideradas en el promedio", example = "4")
    private Integer totalEvaluaciones;
}