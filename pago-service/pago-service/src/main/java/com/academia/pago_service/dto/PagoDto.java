package com.academia.pago_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDto {
    private Long idPago;
    private Double monto;
    private String estado;
    private String runEstudiante;
    private Long idCurso;
    private LocalDateTime fecha;
}
