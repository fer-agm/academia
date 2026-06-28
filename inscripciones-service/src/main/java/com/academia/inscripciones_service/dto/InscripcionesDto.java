package com.academia.inscripciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la transferencia de datos de inscripciones")
public class InscripcionesDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la inscripción (generado automáticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idInscripcion;

    @NotBlank(message = "El RUN del estudiante es obligatorio")
    @Schema(description = "RUN del estudiante que se inscribe", example = "12345678-9")
    private String idEstudiante;

    @NotNull(message = "El curso es obligatorio")
    @Schema(description = "Identificador del curso en el que se inscribe el estudiante", example = "10")
    private Long idCurso;

    @Schema(description = "Fecha y hora de la inscripción (asignada automáticamente por el servidor)", example = "2026-06-27T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaInscripcion;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado de la inscripción", example = "ACTIVO")
    private String estado;
}
