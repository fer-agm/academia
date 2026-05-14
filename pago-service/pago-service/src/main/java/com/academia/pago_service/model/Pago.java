package com.academia.pago_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pagos")
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_pago;

    @Column(name = "run_estudiante")
    private String runEstudiante; 

    private Long id_curso;
    private Double monto;
    private String estado; // "PENDIENTE", "APROBADO", "RECHAZADO"
    private LocalDateTime fecha;
}