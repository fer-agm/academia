package com.academia.calificaciones_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promedio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promedio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promedio")
    private Long idPromedio;

    @Column(name = "id_estudiante", nullable = false)
    private String idEstudiante;

    @Column(name = "id_curso", nullable = false)
    private Long idCurso;

    @Column(name = "promedio_general")
    private Double promedioGeneral;

    @Column(name = "total_evaluaciones")
    private Integer totalEvaluaciones;
}