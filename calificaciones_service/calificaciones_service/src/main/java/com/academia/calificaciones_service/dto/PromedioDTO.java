package com.academia.calificaciones_service.dto;

import org.springframework.hateoas.RepresentationModel;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PromedioDTO extends RepresentationModel<PromedioDTO> {
    private Long idPromedio;

    @NotNull(message = "El ID del curso es obligatorio")
    private Long idCurso;

    @NotBlank(message = "El ID del estudiante es obligatorio")
    private String idEstudiante;

    @Min(value = 1, message = "El promedio mínimo es 1.0")
    @Max(value = 7, message = "El promedio máximo es 7.0")
    private Double promedioGeneral;

    private Integer totalEvaluaciones;
}