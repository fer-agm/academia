package com.academia.inscripciones_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa la inscripción de un estudiante en un curso")
public class Inscripciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la inscripción (generado automáticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id_inscripcion;

    @Column(name = "id_estudiante")
    @NotBlank(message = "El RUN del estudiante es obligatorio")
    @Schema(description = "RUN del estudiante que se inscribe", example = "12345678-9")
    private String idEstudiante;

    @Column(name = "id_curso")
    @NotNull(message = "El curso es obligatorio")
    @Schema(description = "Identificador del curso en el que se inscribe el estudiante", example = "10")
    private Long idCurso;

    @Schema(description = "Fecha y hora de la inscripción (asignada automáticamente por el servidor)", example = "2026-06-27T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fecha_inscripcion;

    @Schema(description = "Estado de la inscripción. Si no se indica, se asigna 'ACTIVO' automáticamente", example = "ACTIVO")
    private String estado;
}
