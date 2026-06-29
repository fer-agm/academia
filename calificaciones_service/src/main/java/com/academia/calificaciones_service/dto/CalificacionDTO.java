package com.academia.calificaciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa la calificación de un estudiante en una evaluación")
public class CalificacionDTO extends RepresentationModel<CalificacionDTO> {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la calificación (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idCalificacion;

    @NotNull(message = "El ID de la evaluación es obligatorio")
    @Schema(description = "Evaluación a la que pertenece esta calificación", example = "10")
    private Long idEvaluacion;

    @NotBlank(message = "El ID del estudiante es obligatorio")
    @Schema(description = "Identificador del estudiante asociado a la calificación", example = "13828053-5")
    private String idEstudiante;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha evaluada no puede ser futura")
    @Schema(description = "Fecha en que se realizó la evaluación (no puede ser futura)", example = "2026-03-15")
    private LocalDate fecha;

    @NotNull(message = "La nota es requerida")
    @Min(value = 1, message = "La nota mínima permitida es 1.0")
    @Max(value = 7, message = "La nota máxima permitida es 7.0")
    @Schema(description = "Nota obtenida en la evaluación (escala de 1.0 a 7.0)", example = "5.8")
    private Double nota;
}
