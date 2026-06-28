package com.academia.calificaciones_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "calificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @Column(name = "id_estudiante", nullable = false)
    private String idEstudiante;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Double nota;
}