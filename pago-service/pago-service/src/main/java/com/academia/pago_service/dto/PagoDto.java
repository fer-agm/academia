package com.academia.pago_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDto {
    private Long idPago;

    @NotBlank(message = "El RUN del estudiante es obligatorio")
    private String runEstudiante;

    @NotNull(message = "El curso es obligatorio")
    private Long idCurso;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private LocalDateTime fecha;
}