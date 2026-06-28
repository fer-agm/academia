package com.academia.calificaciones_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.RepresentationModel;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDTO extends RepresentationModel<CalificacionDTO> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idEvaluacion;

    @NotBlank(message = "El ID del estudiante es obligatorio")
    private String idEstudiante;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha evaluada no puede ser futura") 
    private LocalDate fecha;

    @NotNull(message = "La nota es requerida")
    @Min(value = 1, message = "La nota mínima permitida es 1.0") 
    @Max(value = 7, message = "La nota máxima permitida es 7.0") 
    private Double nota;
}