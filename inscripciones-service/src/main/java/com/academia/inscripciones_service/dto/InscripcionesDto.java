package com.academia.inscripciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionesDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idInscripcion;

    @NotBlank(message = "El RUN del estudiante es obligatorio")
    private String idEstudiante;

    @NotNull(message = "El curso es obligatorio")
    private Long idCurso;

    private LocalDateTime fechaInscripcion;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}